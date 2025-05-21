package ru.practicum.ewm.compilation.service;

import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.dto.NewCompilationDto;
import ru.practicum.ewm.dto.UpdateCompilationRequest;

public interface AdminCompilationService {

    CompilationDto createCompilation(NewCompilationDto newCompilationDto);

    CompilationDto updateCompilation(UpdateCompilationRequest updateCompilationRequest, Long id);

    void deleteCompilationById(Long id);

}
