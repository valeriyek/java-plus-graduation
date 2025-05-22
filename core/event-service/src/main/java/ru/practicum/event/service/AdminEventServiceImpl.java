package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.CategoryServiceClient;
import ru.practicum.client.UserServiceClient;
import ru.practicum.dto.*;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventMapper;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminEventServiceImpl implements AdminEventService {

    private static final Integer HOURS_BEFORE_EVENT_START = 1;

    private final EventRepository eventRepository;
    private final CategoryServiceClient categoryServiceClient;
    private final UserServiceClient userServiceClient;

    @Override
    public List<EventFullDto> findEventByParams(List<Long> userIds, List<EventState> states, List<Long> categoryIds,
                                                LocalDateTime rangeStart, LocalDateTime rangeEnd, Long from, Long size) {
        Pageable pageable = PageRequest.of(from.intValue(), size.intValue());
        Page<Event> eventsPage = eventRepository.findByParams(userIds, states, categoryIds, rangeStart, rangeEnd, pageable);
        List<Event> events = eventsPage.getContent();

        // Собираем уникальные id инициаторов и категорий
        Set<Long> initiatorIds = events.stream().map(Event::getInitiatorId).collect(Collectors.toSet());
        Set<Long> catIds = events.stream().map(Event::getCategoryId).collect(Collectors.toSet());

        // Подгружаем всех инициаторов и категории (через вспомогательные методы)
        Map<Long, UserShortDto> usersById = loadUserShortMap(initiatorIds);
        Map<Long, CategoryDto> categoriesById = loadCategoryMap(catIds);

        return EventMapper.toEventFullDtoList(events, usersById, categoriesById);
    }

    @Transactional
    @Override
    public EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event existEvent = checkEventExist(eventId);

        if (updateEventAdminRequest.getAnnotation() != null) {
            existEvent.setAnnotation(updateEventAdminRequest.getAnnotation());
        }
        if (updateEventAdminRequest.getCategory() != null) {
            CategoryDto categoryDto = categoryServiceClient.getFullCategoriesById(updateEventAdminRequest.getCategory())
                    .orElseThrow(() -> new NotFoundException("Категории c id = " + updateEventAdminRequest.getCategory() + " не существует"));
            existEvent.setCategoryId(categoryDto.getId());
        }
        if (updateEventAdminRequest.getDescription() != null) {
            existEvent.setDescription(updateEventAdminRequest.getDescription());
        }
        if (updateEventAdminRequest.getEventDate() != null) {
            checkIsStartAfterNowPlusHours(updateEventAdminRequest, HOURS_BEFORE_EVENT_START);
            existEvent.setEventDate(updateEventAdminRequest.getEventDate());
        }
        if (updateEventAdminRequest.getLocation() != null) {
            existEvent.setLocation(updateEventAdminRequest.getLocation());
        }
        if (updateEventAdminRequest.getPaid() != null) {
            existEvent.setPaid(updateEventAdminRequest.getPaid());
        }
        if (updateEventAdminRequest.getParticipantLimit() != null) {
            existEvent.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
        }
        if (updateEventAdminRequest.getRequestModeration() != null) {
            existEvent.setRequestModeration(updateEventAdminRequest.getRequestModeration());
        }
        if (updateEventAdminRequest.getStateAction() != null) {
            setEventState(existEvent, updateEventAdminRequest);
        }
        if (updateEventAdminRequest.getTitle() != null) {
            existEvent.setTitle(updateEventAdminRequest.getTitle());
        }

        Event savedEvent = eventRepository.save(existEvent);

        // Получаем данные инициатора и категории для маппера
        UserShortDto initiator = loadUserShort(savedEvent.getInitiatorId());
        CategoryDto category = loadCategory(savedEvent.getCategoryId());

        return EventMapper.toEventFullDto(savedEvent, initiator, category);
    }

    @Transactional
    @Override
    public Event saveEventFull(Event event) {
        return eventRepository.save(event);
    }

    @Override
    public boolean existsByCategoryId(Long id) {
        return eventRepository.existsByCategoryId(id);
    }

    @Override
    public Set<Event> findByIdIn(Set<Long> ids) {
        return eventRepository.findByIdIn(ids);
    }

    // ----------------- Вспомогательные методы для загрузки пользователей и категорий -------------------

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

    // Преобразование UserDto -> UserShortDto (правь под свою структуру)
    private UserShortDto toUserShortDto(UserDto user) {
        UserShortDto dto = new UserShortDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        // Добавь еще поля если они есть в UserShortDto
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

    // ------------------------- Проверки и бизнес-логика ---------------------

    private Event checkEventExist(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("События с id = " + id + " не существует"));
    }

    private void checkIsStartAfterNowPlusHours(UpdateEventAdminRequest updateEventAdminRequest, Integer hours) {
        if (!updateEventAdminRequest.getEventDate().isAfter(LocalDateTime.now().plusHours(hours))) {
            throw new BadRequestException("Дата начала изменяемого события должна быть не ранее чем за " + hours + " час(ов) от даты публикации (текущего времени)");
        }
    }

    private void setEventState(Event event, UpdateEventAdminRequest updateEventAdminRequest) {
        switch (updateEventAdminRequest.getStateAction()) {
            case StateAction.REJECT_EVENT:
                if (event.getState() == EventState.PUBLISHED) {
                    throw new ValidationException("Нельзя отклонить событие, которое находится в статусе опубликовано");
                }
                event.setState(EventState.CANCELED);
                break;
            case StateAction.PUBLISH_EVENT:
                if (event.getState() != EventState.PENDING) {
                    throw new ValidationException("Опубликовать событие можно только если оно находится в статусе ожидания");
                }
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
                break;
        }
    }
}
