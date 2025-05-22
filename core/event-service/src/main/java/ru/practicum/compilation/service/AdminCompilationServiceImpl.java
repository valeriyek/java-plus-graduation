package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.client.EventServiceClient;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.UpdateCompilationRequest;
import ru.practicum.compilation.model.CompilationMapper;
import ru.practicum.exception.NotFoundException;
import ru.practicum.compilation.model.Compilation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminCompilationServiceImpl implements AdminCompilationService {

    private final CompilationRepository compilationRepository;
    private final EventServiceClient eventServiceClient;

    @Override
    @Transactional
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = loadEventsIntoCompilation(newCompilationDto);
        Compilation saved = compilationRepository.save(compilation);

        // Используем batch-эндпоинт!
        Set<Long> eventIds = saved.getEventIds();
        Set<EventShortDto> eventShorts = eventServiceClient.findByIdIn(eventIds);

        Map<Long, EventShortDto> eventShortDtoMap = new HashMap<>();
        for (EventShortDto dto : eventShorts) {
            eventShortDtoMap.put(dto.getId(), dto);
        }

        return CompilationMapper.toCompilationDto(saved, eventShortDtoMap);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(UpdateCompilationRequest updateCompilationRequest, Long id) {
        Compilation compilation = checkExistCompilationById(id);
        if (updateCompilationRequest.getTitle() != null && !updateCompilationRequest.getTitle().isBlank()) {
            compilation.setTitle(updateCompilationRequest.getTitle());
        }
        if (updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        }
        if (updateCompilationRequest.getEvents() != null && !updateCompilationRequest.getEvents().isEmpty()) {
            loadEventsIntoCompilation(compilation, updateCompilationRequest);
        }
        Compilation saved = compilationRepository.save(compilation);

        Set<Long> eventIds = saved.getEventIds();
        Set<EventShortDto> eventShorts = eventServiceClient.findByIdIn(eventIds);

        Map<Long, EventShortDto> eventShortDtoMap = new HashMap<>();
        for (EventShortDto dto : eventShorts) {
            eventShortDtoMap.put(dto.getId(), dto);
        }

        return CompilationMapper.toCompilationDto(saved, eventShortDtoMap);
    }

    @Override
    @Transactional
    public void deleteCompilationById(Long id) {
        checkExistCompilationById(id);
        compilationRepository.deleteById(id);
    }

    private Compilation loadEventsIntoCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = CompilationMapper.toCompilation(newCompilationDto);
        Set<Long> eventIds = newCompilationDto.getEvents();
        compilation.setEventIds(eventIds);
        return compilation;
    }

    private Compilation loadEventsIntoCompilation(Compilation compilation, UpdateCompilationRequest updateCompilationRequest) {
        Set<Long> eventIds = updateCompilationRequest.getEvents();
        compilation.setEventIds(eventIds);
        return compilation;
    }

    private Compilation checkExistCompilationById(Long id) {
        return compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Подборки событий с id = " + id + " не существует"));
    }
}
