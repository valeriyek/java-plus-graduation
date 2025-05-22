package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.client.CategoryServiceClient;
import ru.practicum.client.RequestServiceClient;
import ru.practicum.client.UserServiceClient;
import ru.practicum.dto.*;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventMapper;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {

    private final EventRepository eventRepository;
    private final UserServiceClient userServiceClient;
    private final CategoryServiceClient categoryServiceClient;
    private final RequestServiceClient requestServiceClient;

    private static final long HOURS_BEFORE_EVENT = 2;

    // Список событий пользователя
    public List<EventShortDto> getAllEventsOfUser(Long userId, int from, int size) {
        getUserOrThrow(userId); // Проверка наличия пользователя

        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Event> eventPage = eventRepository.findAllByInitiatorId(userId, pageRequest);

        // Получаем инициатора и категорию для всех событий (оптимизация — пачка, если нужен реальный сервис)
        return eventPage.stream()
                .map(event -> {
                    UserShortDto initiator = getUserShortOrThrow(event.getInitiatorId());
                    CategoryDto category = getCategoryDtoOrThrow(event.getCategoryId());
                    return EventMapper.toEventShortDto(event, initiator, category);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public EventFullDto createEvent(Long userId, NewEventDto dto) {
        UserShortDto initiator = getUserShortOrThrow(userId);
        CategoryDto category = getCategoryDtoOrThrow(dto.getCategory());

        checkEventDate(dto.getEventDate());
        Event event = EventMapper.toEvent(dto, userId, dto.getCategory()); // <-- метод перепиши для userId, categoryId
        Event saved = eventRepository.save(event);
        return EventMapper.toEventFullDto(saved, initiator, category);
    }

    public EventFullDto getEventOfUser(Long userId, Long eventId) {
        getUserOrThrow(userId);
        Event event = getEventOrThrow(eventId);
        if (!event.getInitiatorId().equals(userId)) {
            throw new NotFoundException("Событие не принадлежит пользователю id=" + userId);
        }
        UserShortDto initiator = getUserShortOrThrow(event.getInitiatorId());
        CategoryDto category = getCategoryDtoOrThrow(event.getCategoryId());
        return EventMapper.toEventFullDto(event, initiator, category);
    }

    @Transactional
    public EventFullDto updateEventOfUser(Long userId, Long eventId, UpdateEventUserRequest dto) {
        getUserOrThrow(userId);
        Event event = getEventOrThrow(eventId);
        if (!event.getInitiatorId().equals(userId)) {
            throw new NotFoundException("Событие не принадлежит пользователю id=" + userId);
        }

        if (EventState.PUBLISHED.equals(event.getState())) {
            throw new ConflictException("Нельзя изменять уже опубликованное событие");
        }

        if (dto.getEventDate() != null) {
            checkEventDate(dto.getEventDate());
        }

        Long newCategoryId = dto.getCategory() != null ? dto.getCategory() : event.getCategoryId();
        if (dto.getStateAction() != null) {
            updateState(event, dto.getStateAction());
        }
        EventMapper.updateEventFromUserRequest(event, dto, newCategoryId);
        Event updated = eventRepository.save(event);

        UserShortDto initiator = getUserShortOrThrow(updated.getInitiatorId());
        CategoryDto category = getCategoryDtoOrThrow(updated.getCategoryId());

        return EventMapper.toEventFullDto(updated, initiator, category);
    }

    // ----------------- ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ----------------

    private void updateState(Event event, String stateAction) {
        switch (stateAction) {
            case "CANCEL_REVIEW":
                if (EventState.PENDING.equals(event.getState())) {
                    event.setState(EventState.CANCELED);
                } else {
                    throw new ConflictException("Событие можно отменить только в состоянии PENDING.");
                }
                break;
            case "SEND_TO_REVIEW":
                if (EventState.PENDING.equals(event.getState()) || EventState.CANCELED.equals(event.getState())) {
                    event.setState(EventState.PENDING);
                } else {
                    throw new ConflictException("Событие можно отправить на модерацию только в состоянии PENDING.");
                }
                break;
            default:
                throw new ValidationException("Некорректное значение stateAction: " + stateAction);
        }
    }

    private Event getEventOrThrow(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Событие с id=" + id + " не найдено"));
    }

    private CategoryDto getCategoryDtoOrThrow(Long catId) {
        return categoryServiceClient.getFullCategoriesById(catId)
                .orElseThrow(() -> new NotFoundException("Категория с id=" + catId + " не найдена"));
    }

    private UserShortDto getUserShortOrThrow(Long userId) {
        UserDto userDto = userServiceClient.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
        return toUserShortDto(userDto);
    }
    private UserShortDto toUserShortDto(UserDto userDto) {
        UserShortDto dto = new UserShortDto();
        dto.setId(userDto.getId());
        dto.setName(userDto.getName());

        return dto;
    }


    private void getUserOrThrow(Long userId) {

        userServiceClient.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
    }

    private void checkEventDate(LocalDateTime eventDate) {
        if (eventDate.isBefore(LocalDateTime.now().plusHours(HOURS_BEFORE_EVENT))) {
            throw new ConflictException(
                    "Дата события не может быть раньше, чем через " + HOURS_BEFORE_EVENT + " часа(ов) от текущего момента."
            );
        }
    }

    private Long getConfirmedRequests(Long eventId) {
        return requestServiceClient.getCountConfirmedRequestsByEventId(eventId);
    }
}
