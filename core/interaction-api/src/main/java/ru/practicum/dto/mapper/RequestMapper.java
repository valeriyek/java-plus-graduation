package ru.practicum.dto.mapper;

import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.model.ParticipationRequest;

public class RequestMapper {

    public static ParticipationRequestDto toParticipationRequestDto(ParticipationRequest request) {
        ParticipationRequestDto dto = new ParticipationRequestDto();
        dto.setId(request.getId());
        dto.setCreated(request.getCreated());
        dto.setEvent(request.getEvent().getId());
        dto.setRequester(request.getRequester().getId());
        dto.setStatus(request.getStatus().name());
        return dto;
    }
}
