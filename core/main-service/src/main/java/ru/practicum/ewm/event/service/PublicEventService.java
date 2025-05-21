package ru.practicum.ewm.event.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.EventShortDto;
import ru.practicum.ewm.dto.EventSort;

import java.time.LocalDateTime;
import java.util.List;

public interface PublicEventService {
    EventFullDto getEventById(long id, HttpServletRequest request);

    List<EventShortDto> getEvents(String text,
                                  List<Long> categories,
                                  Boolean paid,
                                  LocalDateTime rangeStart,
                                  LocalDateTime rangeEnd,
                                  Boolean onlyAvailable,
                                  EventSort sort,
                                  int from,
                                  int size,
                                  HttpServletRequest request);
}
