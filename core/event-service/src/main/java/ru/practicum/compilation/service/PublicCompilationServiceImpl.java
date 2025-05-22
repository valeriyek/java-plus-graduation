package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.client.EventServiceClient;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.compilation.model.CompilationMapper;
import ru.practicum.exception.NotFoundException;
import ru.practicum.compilation.model.Compilation;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PublicCompilationServiceImpl implements PublicCompilationService {
    private final CompilationRepository compilationRepository;
    private final EventServiceClient eventServiceClient;

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getCompilationById(long id) {
        Compilation compilation = compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Подборка с " + id + " не найдена"));

        // Собрать eventIds из подборки
        Set<Long> eventIds = compilation.getEventIds();
        Set<EventShortDto> eventShorts = eventServiceClient.findByIdIn(eventIds);
        Map<Long, EventShortDto> eventShortDtoMap = new HashMap<>();
        for (EventShortDto dto : eventShorts) {
            eventShortDtoMap.put(dto.getId(), dto);
        }

        return CompilationMapper.toCompilationDto(compilation, eventShortDtoMap);
    }

    @Override
    public List<CompilationDto> getAllCompilations(Boolean pinned, int from, int size) {
        PageRequest page = PageRequest.of(from, size);
        Page<Compilation> pageCompilations;
        if (pinned != null) {
            pageCompilations = compilationRepository.findAllByPinned(pinned, page);
        } else {
            pageCompilations = compilationRepository.findAll(page);
        }

        List<Compilation> compilations = pageCompilations.getContent();

        // Собираем все eventIds со всех подборок, чтобы одним запросом получить все EventShortDto
        Set<Long> allEventIds = new HashSet<>();
        for (Compilation c : compilations) {
            allEventIds.addAll(c.getEventIds());
        }

        Set<EventShortDto> allEventShorts = eventServiceClient.findByIdIn(allEventIds);
        Map<Long, EventShortDto> eventShortDtoMap = new HashMap<>();
        for (EventShortDto dto : allEventShorts) {
            eventShortDtoMap.put(dto.getId(), dto);
        }

        List<CompilationDto> compilationsDto = new ArrayList<>();
        for (Compilation c : compilations) {
            compilationsDto.add(CompilationMapper.toCompilationDto(c, eventShortDtoMap));
        }

        log.info("получен список compilationsDto from = " + from + " size " + size);
        return compilationsDto;
    }
}
