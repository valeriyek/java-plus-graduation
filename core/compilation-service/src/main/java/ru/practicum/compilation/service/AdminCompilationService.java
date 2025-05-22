package ru.practicum.compilation.service;

import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.UpdateCompilationRequest;

public interface AdminCompilationService {

    CompilationDto createCompilation(NewCompilationDto newCompilationDto);

    CompilationDto updateCompilation(UpdateCompilationRequest updateCompilationRequest, Long id);

    void deleteCompilationById(Long id);

}
