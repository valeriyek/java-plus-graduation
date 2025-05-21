package ru.practicum.ewm.event.service;

import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.EventState;
import ru.practicum.ewm.dto.UpdateEventAdminRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface AdminEventService {

    List<EventFullDto> findEventByParams(List<Long> userIds, List<EventState> states, List<Long> categoryIds,
                                         LocalDateTime rangeStart, LocalDateTime rangeEnd, Long from, Long size);

    EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

}
