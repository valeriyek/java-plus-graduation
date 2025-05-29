package ru.practicum.compilation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.dto.EventShortDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CompilationMapper {

    Compilation toCompilation(CompilationDto compilationDto);

    @Mapping(target = "events", source = "eventDtos")
    CompilationDto toCompilationDto(Compilation compilation, List<EventShortDto> eventDtos);

    @Mapping(target = "events", ignore = true)
    List<CompilationDto> toCompilationDtos(List<Compilation> compilations);
}