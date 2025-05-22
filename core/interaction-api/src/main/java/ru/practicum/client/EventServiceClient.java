package ru.practicum.client;

import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventShortDto;


import java.util.Optional;
import java.util.Set;


@FeignClient(name = "event-service", contextId = "eventServiceClient")
public interface EventServiceClient {

    @GetMapping("/admin/events/existsbycategory/{id}")
    boolean existsByCategoryId(@PathVariable Long id) throws FeignException;

    @GetMapping("/events/{id}/full")
    Optional<EventFullDto> getEventFullById(@PathVariable long id) throws FeignException;

    @PostMapping("/admin/events")
    EventFullDto saveEvent(@RequestBody EventFullDto event) throws FeignException;

    @GetMapping("/admin/events/findbyidin")
    Set<EventShortDto> findByIdIn(@RequestParam Set<Long> ids) throws FeignException;
}
