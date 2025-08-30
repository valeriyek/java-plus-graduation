package ru.practicum.compilation.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.service.CompilationService;

import java.util.Collection;
/**
 * Публичный REST-контроллер для чтения подборок событий.
 * <p>Доступен без авторизации. Предоставляет только операции получения.</p>
 *
 * <ul>
 *   <li>GET /compilations — постраничное получение списка подборок
 *       (с возможной фильтрацией по признаку {@code pinned});</li>
 *   <li>GET /compilations/{compId} — получение конкретной подборки по идентификатору.</li>
 * </ul>
 *
 * <p>Бизнес-логика реализована в {@link ru.practicum.compilation.service.CompilationService}.</p>
 */
@RestController
@RequestMapping(path = "/compilations")
@RequiredArgsConstructor
public class PublicCompilationController {
    private final CompilationService compilationService;

    @GetMapping
    public Collection<CompilationDto> getCompilations(@RequestParam(name = "pinned", required = false) Boolean pinned,
                                                      @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                      @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return compilationService.getCompilations(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilationById(@PathVariable(name = "compId") Long compId) {
        return compilationService.getCompilationById(compId);
    }
}