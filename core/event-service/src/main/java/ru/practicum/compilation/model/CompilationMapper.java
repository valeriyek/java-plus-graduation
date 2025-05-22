package ru.practicum.compilation.model;

import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.EventShortDto;

import java.util.*;
import java.util.stream.Collectors;

public class CompilationMapper {

    public static CompilationDto toCompilationDto(
            Compilation compilation,
            Map<Long, EventShortDto> eventShortDtoMap
    ) {
        CompilationDto dto = new CompilationDto();
        dto.setId(compilation.getId());
        dto.setPinned(compilation.getPinned());
        dto.setTitle(compilation.getTitle());

        Set<EventShortDto> events = compilation.getEventIds().stream()
                .map(eventShortDtoMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        dto.setEvents(events);
        return dto;
    }

    public static List<CompilationDto> toCompilationDto(
            Iterable<Compilation> compilations,
            Map<Long, EventShortDto> eventShortDtoMap
    ) {
        List<CompilationDto> result = new ArrayList<>();
        for (Compilation compilation : compilations) {
            result.add(toCompilationDto(compilation, eventShortDtoMap));
        }
        return result;
    }

    public static Compilation toCompilation(NewCompilationDto newDto) {
        Compilation compilation = new Compilation();
        compilation.setPinned(newDto.getPinned());
        compilation.setTitle(newDto.getTitle());
        // События добавляешь отдельно, если надо
        return compilation;
    }
}
