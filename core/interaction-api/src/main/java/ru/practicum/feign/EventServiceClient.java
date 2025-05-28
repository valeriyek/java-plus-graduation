package ru.practicum.feign;

import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.NewEventDto;



import java.util.Optional;
import java.util.Set;


//@FeignClient(name = "event-service", contextId = "eventServiceClient")
//public interface EventServiceClient {
//
//    @GetMapping("/admin/events/existsbycategory/{id}")
//    boolean existsByCategoryId(@PathVariable Long id) throws FeignException;
//
//    @GetMapping("/events/{id}/full")
//    Optional<Event> getEventFullById(@PathVariable long id) throws FeignException;
//
//    @PostMapping("/admin/events")
//    Event saveEvent(@RequestBody Event event) throws FeignException;
//
//    @GetMapping("/admin/events/findbyidin")
//    Set<Event> findByIdIn(@RequestParam Set<Long> ids) throws FeignException;
//}
@FeignClient(name = "event-service", contextId = "eventServiceClient")
public interface EventServiceClient {

    @GetMapping("/admin/events/existsbycategory/{id}")
    boolean existsByCategoryId(@PathVariable Long id) throws FeignException;

    @GetMapping("/events/{id}/full")
    Optional<EventFullDto> getEventFullById(@PathVariable long id) throws FeignException;

    @PostMapping("/admin/events")
    EventFullDto saveEvent(@RequestBody NewEventDto eventDto) throws FeignException;

    @GetMapping("/admin/events/findbyidin")
    Set<EventShortDto> findByIdIn(@RequestParam Set<Long> ids) throws FeignException;


}