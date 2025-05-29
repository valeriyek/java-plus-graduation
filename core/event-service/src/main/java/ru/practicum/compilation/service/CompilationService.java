package ru.practicum.compilation.service;



import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;

import java.util.Collection;

public interface CompilationService {

    CompilationDto create(NewCompilationDto newCompilationDto);

    void delete(Long id);

    CompilationDto update(Long id, UpdateCompilationRequest updateCompilationRequest);

    Collection<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size);

    CompilationDto getCompilationById(Long compId);
}