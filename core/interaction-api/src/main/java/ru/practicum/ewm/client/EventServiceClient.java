package ru.practicum.ewm.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.EventShortDto;


@FeignClient(name = "event-service", path = "/events")
public interface EventServiceClient {

    @GetMapping("/{eventId}")
    EventFullDto getEventById(@PathVariable("eventId") Long eventId);
    @PatchMapping("/events/{id}/confirmed")
    void updateConfirmedRequests(@PathVariable Long id, @RequestParam Long count);
}
