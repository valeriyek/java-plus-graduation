package ru.practicum.event.model;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.category.model.CategoryMapper;
import ru.practicum.dto.*;
import ru.practicum.exception.NotFoundException;
import ru.practicum.feign.UserServiceClient;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EventMapper {

    private final UserServiceClient userServiceClient;

    public static Event toEvent(NewEventDto dto) {
        Event event = new Event();
        event.setAnnotation(dto.getAnnotation());
        event.setDescription(dto.getDescription());
        event.setEventDate(dto.getEventDate());
        event.setLocation(dto.getLocation());
        event.setPaid(Boolean.TRUE.equals(dto.getPaid()));
        event.setParticipantLimit(dto.getParticipantLimit() != null ? dto.getParticipantLimit() : 0);
        event.setRequestModeration(Boolean.TRUE.equals(dto.getRequestModeration()));
        event.setTitle(dto.getTitle());
        event.setState(EventState.PENDING);
        event.setCreatedOn(LocalDateTime.now());
        event.setViews(0L);
        event.setConfirmedRequests(0L);
        return event;
    }

    public static void updateEventFromUserRequest(Event event, UpdateEventUserRequest dto) {
        if (dto.getAnnotation() != null) event.setAnnotation(dto.getAnnotation());
        if (dto.getDescription() != null) event.setDescription(dto.getDescription());
        if (dto.getEventDate() != null) event.setEventDate(dto.getEventDate());
        if (dto.getLocation() != null) event.setLocation(dto.getLocation());
        if (dto.getPaid() != null) event.setPaid(dto.getPaid());
        if (dto.getParticipantLimit() != null) event.setParticipantLimit(dto.getParticipantLimit());
        if (dto.getRequestModeration() != null) event.setRequestModeration(dto.getRequestModeration());
        if (dto.getTitle() != null) event.setTitle(dto.getTitle());
    }

    public EventFullDto toEventFullDto(Event event) {
        UserShortDto userDto = userServiceClient.getUserById(event.getInitiator())
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + event.getInitiator() + " не найден"));

        return toEventFullDto(event, userDto);
    }

    public EventFullDto toEventFullDto(Event event, UserShortDto userDto) {
        EventFullDto dto = new EventFullDto();
        dto.setId(event.getId());
        dto.setAnnotation(event.getAnnotation());
        dto.setDescription(event.getDescription());
        dto.setEventDate(event.getEventDate());
        dto.setLocation(event.getLocation());
        dto.setPaid(event.isPaid());
        dto.setParticipantLimit(event.getParticipantLimit());
        dto.setRequestModeration(event.isRequestModeration());
        dto.setState(event.getState());
        dto.setTitle(event.getTitle());
        dto.setCreatedOn(event.getCreatedOn());
        dto.setPublishedOn(event.getPublishedOn());
        dto.setInitiator(userDto);
        dto.setConfirmedRequests(Optional.ofNullable(event.getConfirmedRequests()).orElse(0L));
        dto.setViews(Optional.ofNullable(event.getViews()).orElse(0L));
        return dto;
    }

    public List<EventFullDto> toEventFullDto(List<Event> events) {
        List<Long> userIds = events.stream().map(Event::getInitiator).distinct().toList();
        Map<Long, UserShortDto> users = loadUsers(userIds);

        return events.stream()
                .map(e -> toEventFullDto(e, users.get(e.getInitiator())))
                .collect(Collectors.toList());
    }

    public EventShortDto toEventShortDto(Event event) {
        UserShortDto userDto = userServiceClient.getUserById(event.getInitiator())
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + event.getInitiator() + " не найден"));

        return toEventShortDto(event, userDto);
    }

    public EventShortDto toEventShortDto(Event event, UserShortDto userDto) {
        EventShortDto dto = new EventShortDto();
        dto.setId(event.getId());
        dto.setAnnotation(event.getAnnotation());
        dto.setEventDate(event.getEventDate());
        dto.setPaid(event.isPaid());
        dto.setTitle(event.getTitle());
        dto.setConfirmedRequests(Optional.ofNullable(event.getConfirmedRequests()).orElse(0L));
        dto.setViews(Optional.ofNullable(event.getViews()).orElse(0L));
        dto.setInitiator(userDto);
       return dto;
    }

    public List<EventShortDto> toEventShortDto(List<Event> events) {
        List<Long> userIds = events.stream().map(Event::getInitiator).distinct().toList();
        Map<Long, UserShortDto> users = loadUsers(userIds);

        return events.stream()
                .map(e -> toEventShortDto(e, users.get(e.getInitiator())))
                .collect(Collectors.toList());
    }

    private Map<Long, UserShortDto> loadUsers(List<Long> ids) {
        return userServiceClient.getUsersWithIds(ids).stream()
                .collect(Collectors.toMap(UserShortDto::getId, user -> user));
    }
}
