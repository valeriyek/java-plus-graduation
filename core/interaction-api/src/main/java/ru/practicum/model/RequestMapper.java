package ru.practicum.model;

import ru.practicum.dto.ParticipationRequestDto;


public class RequestMapper {

    public static ParticipationRequestDto toParticipationRequestDto(ParticipationRequest request) {
        ParticipationRequestDto dto = new ParticipationRequestDto();
        dto.setId(request.getId());
        dto.setCreated(request.getCreated());
        //dto.setEvent(request.getEvent().getId());
        //dto.setRequester(request.getRequester().getId());
        dto.setEvent(request.getEventId());
        dto.setRequester(request.getRequesterId());
        dto.setStatus(request.getStatus().name());
        return dto;
    }
}
