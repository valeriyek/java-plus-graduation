package ru.practicum.event.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.EndpointHitInputDto;
import ru.practicum.StatFeignClient;
import ru.practicum.ViewStatsOutputDto;
import ru.practicum.client.CategoryServiceClient;
import ru.practicum.client.UserServiceClient;
import ru.practicum.dto.*;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventMapper;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PublicEventServiceImpl implements PublicEventService {

    @Value("${app.name:ewm-main}")
    private String APP_NAME;

    private final EventRepository eventRepository;
    private final StatFeignClient statsClient;
    private final CategoryServiceClient categoryServiceClient;
    private final UserServiceClient userServiceClient;

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventById(long id, HttpServletRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event c id " + id + " не найден"));

        if (event.getState() != EventState.PUBLISHED) {
            throw new NotFoundException("Event c id " + id + " еще не опубликован");
        }

        addHit(request);
        Event eventWithViews = updateEventViewsInRepository(event);

        // Получаем инициатора и категорию для EventFullDto
        UserShortDto initiator = loadUserShort(eventWithViews.getInitiatorId());
        CategoryDto category = loadCategory(eventWithViews.getCategoryId());

        EventFullDto eventFullDto = EventMapper.toEventFullDto(eventWithViews, initiator, category);

        log.info("получен eventFullDto с ID = {}", eventFullDto.getId());
        return eventFullDto;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Event> getEventFullById(long id) {
        return eventRepository.findById(id);
    }

    @Override
    public List<EventShortDto> getEvents(String text,
                                         List<Long> categories,
                                         Boolean paid,
                                         LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd,
                                         Boolean onlyAvailable,
                                         EventSort sort,
                                         int from,
                                         int size,
                                         HttpServletRequest request) {

        LocalDateTime start = rangeStart != null ? rangeStart : LocalDateTime.now();
        LocalDateTime end = rangeEnd != null ? rangeEnd : LocalDateTime.now().plusYears(1);

        if (end.isBefore(start)) {
            throw new BadRequestException("Недопустимый временной промежуток, время окончания поиска не может быть раньше времени начала поиска");
        }

        PageRequest page = PageRequest.of(from, size);
        Page<Event> pageEvents;
        if (onlyAvailable != null && onlyAvailable) {
            pageEvents = eventRepository.findAllByPublicFiltersAndOnlyAvailable(text, categories, paid, start, end, page);
        } else {
            pageEvents = eventRepository.findAllByPublicFilters(text, categories, paid, start, end, page);
        }

        List<Event> events = pageEvents.getContent();

        addHit(request);

        // Собрать набор уникальных id инициаторов и категорий для батч-загрузки
        Set<Long> initiatorIds = events.stream().map(Event::getInitiatorId).collect(Collectors.toSet());
        Set<Long> categoryIds = events.stream().map(Event::getCategoryId).collect(Collectors.toSet());

        // Подгрузить пользователей и категории
        Map<Long, UserShortDto> usersById = loadUserShortMap(initiatorIds);
        Map<Long, CategoryDto> categoriesById = loadCategoryMap(categoryIds);

        // Обновить просмотры, собрать DTO
        List<EventShortDto> eventShortDtos = new ArrayList<>();
        for (Event event : events) {
            Event ev = updateEventViewsInRepository(event);
            UserShortDto initiator = usersById.get(ev.getInitiatorId());
            CategoryDto category = categoriesById.get(ev.getCategoryId());
            EventShortDto dto = EventMapper.toEventShortDto(ev, initiator, category);
            eventShortDtos.add(dto);
        }

        if (sort != null) {
            if (sort.equals(EventSort.EVENT_DATE)) {
                eventShortDtos.sort(Comparator.comparing(EventShortDto::getEventDate));
            } else {
                eventShortDtos.sort(Comparator.comparing(EventShortDto::getViews, Comparator.reverseOrder()));
            }
        } else {
            eventShortDtos.sort(Comparator.comparing(EventShortDto::getViews, Comparator.reverseOrder()));
        }

        return eventShortDtos;
    }

    // ---------- Вспомогательные методы для пользователей и категорий -----------

    private Map<Long, UserShortDto> loadUserShortMap(Set<Long> userIds) {
        Map<Long, UserShortDto> map = new HashMap<>();
        for (Long id : userIds) {
            UserShortDto user = loadUserShort(id);
            if (user != null) {
                map.put(id, user);
            }
        }
        return map;
    }

    private UserShortDto loadUserShort(Long id) {
        Optional<UserDto> userOpt = userServiceClient.getUserById(id);
        return userOpt.map(this::toUserShortDto).orElse(null);
    }

    // Преобразование UserDto -> UserShortDto (подстрой под свою структуру)
    private UserShortDto toUserShortDto(UserDto user) {
        UserShortDto dto = new UserShortDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        // Добавь еще поля, если есть
        return dto;
    }

    private Map<Long, CategoryDto> loadCategoryMap(Set<Long> categoryIds) {
        Map<Long, CategoryDto> map = new HashMap<>();
        for (Long id : categoryIds) {
            CategoryDto category = loadCategory(id);
            if (category != null) {
                map.put(id, category);
            }
        }
        return map;
    }

    private CategoryDto loadCategory(Long id) {
        return categoryServiceClient.getFullCategoriesById(id).orElse(null);
    }

    // ---------- Методы статистики и просмотров -----------

    private void addHit(HttpServletRequest request) {
        EndpointHitInputDto hit = new EndpointHitInputDto();
        hit.setApp(APP_NAME);
        hit.setUri(request.getRequestURI());
        hit.setIp(request.getRemoteAddr());
        hit.setTimestamp(LocalDateTime.now());
        statsClient.addHit(hit);
    }

    private Event updateEventViewsInRepository(Event event) {
        try {
            Long eventId = event.getId();
            String eventUri = "/events/" + eventId;
            ResponseEntity<Object> responseEntity = statsClient.getStats(
                    LocalDateTime.now().minusYears(999),
                    LocalDateTime.now().plusYears(1),
                    List.of(eventUri),
                    true
            );

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                ObjectMapper objectMapper = new ObjectMapper();
                List<Map<String, Object>> responseBody = objectMapper.convertValue(responseEntity.getBody(), new TypeReference<List<Map<String, Object>>>() {
                });

                List<ViewStatsOutputDto> responseList = responseBody.stream()
                        .map(map -> new ViewStatsOutputDto((String) map.get("app"), (String) map.get("uri"), ((Number) map.get("hits")).longValue()))
                        .toList();

                if (!responseList.isEmpty()) {
                    ViewStatsOutputDto viewStatsOutputDto = responseList.getFirst();
                    event.setViews(viewStatsOutputDto.getHits());
                    return eventRepository.save(event);
                }
            }
            return event;
        } catch (Exception e) {
            return event;
        }
    }
}
