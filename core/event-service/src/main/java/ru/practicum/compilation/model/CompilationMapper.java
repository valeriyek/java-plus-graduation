package ru.practicum.compilation.model;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.UserShortDto;
import ru.practicum.feign.EventServiceClient;
import ru.practicum.feign.UserServiceClient;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CompilationMapper {

    private final UserServiceClient userServiceClient;
    private final EventServiceClient eventServiceClient;


    public CompilationDto toCompilationDto(Compilation compilation) {
        CompilationDto dto = new CompilationDto();
        dto.setId(compilation.getId());
        dto.setTitle(compilation.getTitle());
        dto.setPinned(compilation.getPinned());
        dto.setEvents(mapShortAndAddUsers(compilation.getEventIds()));
        return dto;
    }

    public List<CompilationDto> toCompilationDto(Iterable<Compilation> compilations) {
        List<CompilationDto> result = new ArrayList<>();
        for (Compilation compilation : compilations) {
            result.add(toCompilationDto(compilation));
        }
        return result;
    }

    public static Compilation toCompilation(NewCompilationDto newDto) {
        Compilation compilation = new Compilation();
        compilation.setTitle(newDto.getTitle());
        compilation.setPinned(newDto.getPinned());
        compilation.setEventIds(newDto.getEvents() != null ? new HashSet<>(newDto.getEvents()) : new HashSet<>());
        return compilation;
    }

    private Set<EventShortDto> mapShortAndAddUsers(Set<Long> eventIds) {
        if (eventIds == null || eventIds.isEmpty()) return Collections.emptySet();

        Set<EventShortDto> eventShortDtos = eventServiceClient.findByIdIn(eventIds);
        List<Long> userIds = eventShortDtos.stream()
                .map(EventShortDto::getInitiator)
                .filter(Objects::nonNull)
                .map(UserShortDto::getId) // <-- вот тут ключевой момент
                .filter(Objects::nonNull)
                .distinct()
                .toList();


        Map<Long, UserShortDto> usersById = userServiceClient.getUsersWithIds(userIds).stream()
                .collect(Collectors.toMap(UserShortDto::getId, u -> u));

        return eventShortDtos.stream()
                .map(event -> {
                    UserShortDto user = usersById.get(event.getInitiator().getId());
                    EventShortDto dto = new EventShortDto();
                    dto.setId(event.getId());
                    dto.setAnnotation(event.getAnnotation());
                    dto.setEventDate(event.getEventDate());
                    dto.setPaid(event.isPaid());
                    dto.setTitle(event.getTitle());
                    dto.setConfirmedRequests(event.getConfirmedRequests());
                    dto.setViews(event.getViews());
                    dto.setInitiator(user);
                    dto.setCategory(event.getCategory());
                    return dto;
                })

                .collect(Collectors.toSet());
    }
}
