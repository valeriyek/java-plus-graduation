package ru.practicum.client;



import ru.practicum.ParamDto;
import ru.practicum.ParamHitDto;
import ru.practicum.ViewStats;

import java.util.List;

public interface StatClient {
    void hit(ParamHitDto paramHitDto);

    List<ViewStats> getStat(ParamDto paramDto);
}