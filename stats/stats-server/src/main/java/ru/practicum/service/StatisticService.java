package ru.practicum.service;

import ru.practicum.EndpointHitInputDto;
import ru.practicum.ViewStatsOutputDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatisticService {

    void createEndpointHit(EndpointHitInputDto endpointHitInputDto);

    List<ViewStatsOutputDto> getStats(List<String> uris, LocalDateTime start, LocalDateTime end, Boolean unique);
}
