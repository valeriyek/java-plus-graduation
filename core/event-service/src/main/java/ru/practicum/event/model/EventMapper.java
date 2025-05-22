package ru.practicum.event.model;



import ru.practicum.dto.*;




import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EventMapper {

    public static Event toEvent(NewEventDto dto, Long initiatorId, Long categoryId) {
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
        event.setInitiatorId(initiatorId);
        event.setCategoryId(categoryId);
        event.setViews(0L);
        event.setConfirmedRequests(0L);

        return event;
    }

    public static void updateEventFromUserRequest(Event event, UpdateEventUserRequest dto, Long categoryId) {
        if (dto.getAnnotation() != null) event.setAnnotation(dto.getAnnotation());
        if (dto.getDescription() != null) event.setDescription(dto.getDescription());
        if (dto.getEventDate() != null) event.setEventDate(dto.getEventDate());
        if (dto.getLocation() != null) event.setLocation(dto.getLocation());
        if (dto.getPaid() != null) event.setPaid(dto.getPaid());
        if (dto.getParticipantLimit() != null) event.setParticipantLimit(dto.getParticipantLimit());
        if (dto.getRequestModeration() != null) event.setRequestModeration(dto.getRequestModeration());
        if (dto.getTitle() != null) event.setTitle(dto.getTitle());
        if (dto.getCategory() != null) event.setCategoryId(categoryId);
    }







    public static EventShortDto toEventShortDto(Event event, UserShortDto initiator, CategoryDto category) {
        EventShortDto dto = new EventShortDto();
        dto.setId(event.getId());
        dto.setAnnotation(event.getAnnotation());
        dto.setEventDate(event.getEventDate());
        dto.setPaid(event.isPaid());
        dto.setTitle(event.getTitle());
        dto.setConfirmedRequests(event.getConfirmedRequests() != null ? event.getConfirmedRequests() : 0L);
        dto.setViews(event.getViews() != null ? event.getViews() : 0L);
        dto.setInitiatorId(event.getInitiatorId());
        dto.setCategoryId(event.getCategoryId());
        return dto;
    }

    public static List<EventShortDto> toEventShortDto(Iterable<Event> events) {
        List<EventShortDto> result = new ArrayList<>();
        for (Event event : events) {
            EventShortDto dto = new EventShortDto();
            dto.setId(event.getId());
            dto.setAnnotation(event.getAnnotation());
            dto.setEventDate(event.getEventDate());
            dto.setPaid(event.isPaid());
            dto.setTitle(event.getTitle());
            dto.setConfirmedRequests(event.getConfirmedRequests() != null ? event.getConfirmedRequests() : 0L);
            dto.setViews(event.getViews() != null ? event.getViews() : 0L);
            dto.setInitiatorId(event.getInitiatorId());
            dto.setCategoryId(event.getCategoryId());
            result.add(dto);
        }
        return result;
    }

    public static EventFullDto toEventFullDto(Event event, UserShortDto initiator, CategoryDto category) {
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
        dto.setInitiator(initiator);
        dto.setCategory(category);
        dto.setConfirmedRequests(event.getConfirmedRequests() != null ? event.getConfirmedRequests() : 0L);
        dto.setViews(event.getViews() != null ? event.getViews() : 0L);
        return dto;
    }

    public static List<EventFullDto> toEventFullDtoList(
            List<Event> events,
            Map<Long, UserShortDto> usersById,
            Map<Long, CategoryDto> categoriesById
    ) {
        List<EventFullDto> result = new ArrayList<>();
        for (Event event : events) {
            UserShortDto initiator = usersById.get(event.getInitiatorId());
            CategoryDto category = categoriesById.get(event.getCategoryId());
            result.add(toEventFullDto(event, initiator, category));
        }
        return result;
    }



}
