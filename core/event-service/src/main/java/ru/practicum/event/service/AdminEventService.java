package ru.practicum.event.service;

import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventState;
import ru.practicum.dto.UpdateEventAdminRequest;
import ru.practicum.model.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface AdminEventService {

    List<EventFullDto> findEventByParams(List<Long> userIds, List<EventState> states, List<Long> categoryIds,
                                         LocalDateTime rangeStart, LocalDateTime rangeEnd, Long from, Long size);

    EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    Event saveEventFull(Event event);

    boolean existsByCategoryId(Long id);

    Set<Event> findByIdIn(Set<Long> ids);
}
