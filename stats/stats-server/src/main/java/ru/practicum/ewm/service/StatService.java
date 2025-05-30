package ru.practicum.ewm.service;



import ru.practicum.ParamDto;
import ru.practicum.ParamHitDto;
import ru.practicum.ViewStats;

import java.util.List;

public interface StatService {
    void hit(ParamHitDto paramHitDto);

    List<ViewStats> getStat(ParamDto paramDto);
}
