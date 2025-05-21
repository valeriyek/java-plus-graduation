package ru.practicum.ewm.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.dto.ParticipationRequestDto;


import java.util.List;

@FeignClient(name = "request-service", path = "/requests", configuration = FeignConfig.class)
public interface RequestServiceClient {

    @GetMapping("/user/{userId}")
    List<ParticipationRequestDto> getUserRequests(@PathVariable Long userId);

    @PostMapping
    ParticipationRequestDto addRequest(@RequestParam Long userId, @RequestParam Long eventId);

    @PatchMapping("/{requestId}/cancel")
    ParticipationRequestDto cancelRequest(@PathVariable Long requestId, @RequestParam Long userId);

    @GetMapping("/event/{eventId}")
    List<ParticipationRequestDto> getRequestsForEvent(@PathVariable Long eventId, @RequestParam Long userId);

    @PatchMapping("/event/{eventId}/status")
    EventRequestStatusUpdateResult changeRequestsStatus(@PathVariable Long eventId,
                                                        @RequestParam Long userId,
                                                        @RequestBody EventRequestStatusUpdateRequest request);

    @GetMapping("/internal/{eventId}/confirmed-count")
    Long getConfirmedRequests(@PathVariable Long eventId);
}
