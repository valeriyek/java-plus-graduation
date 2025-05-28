package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.UserServiceClient;
import ru.practicum.dto.*;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ParticipantLimitReachedException;
import ru.practicum.exception.ValidationException;
import ru.practicum.feign.EventServiceClient;
import ru.practicum.request.model.ParticipationRequest;
import ru.practicum.request.model.RequestMapper;

import ru.practicum.request.repository.RequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestService {

    private final RequestRepository requestRepository;
    private final EventServiceClient eventServiceClient;
    private final UserServiceClient userServiceClient;

    public List<ParticipationRequestDto> getRequestsOfUser(Long userId) {
        getUserOrThrow(userId);
        return requestRepository.findAllByRequester(userId).stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ParticipationRequestDto addRequest(Long userId, Long eventId) {
        getUserOrThrow(userId);
        EventFullDto event = getEventOrThrow(eventId);

        if (event.getInitiator().getId().equals(userId)) {
            throw new ValidationException("Инициатор события не может добавить запрос на участие в своём событии.");
        }

        if (event.getState() != EventState.PUBLISHED) {
            throw new ValidationException("Нельзя участвовать в неопубликованном событии.");
        }

        if (requestRepository.existsByRequesterAndEvent(userId, eventId)) {
            throw new ValidationException("Нельзя повторно подавать заявку на то же событие.");
        }

        if (event.getParticipantLimit() != 0 &&
                event.getConfirmedRequests() != null &&
                event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new ParticipantLimitReachedException("Лимит участников уже достигнут");
        }

        ParticipationRequest request = new ParticipationRequest();
        request.setCreated(LocalDateTime.now());
        request.setEvent(eventId);
        request.setRequester(userId);

        boolean autoConfirm = !event.isRequestModeration() || event.getParticipantLimit() == 0;
        request.setStatus(autoConfirm ? RequestStatus.CONFIRMED : RequestStatus.PENDING);

        ParticipationRequest savedRequest = requestRepository.save(request);

        return RequestMapper.toParticipationRequestDto(savedRequest);
    }

    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        ParticipationRequest request = requestRepository.findByIdAndRequester(requestId, userId)
                .orElseThrow(() -> new NotFoundException("Заявка не найдена или не принадлежит пользователю."));
        request.setStatus(RequestStatus.CANCELED);
        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    public List<ParticipationRequestDto> getRequestsForUserEvent(Long userId, Long eventId) {
        getUserOrThrow(userId);
        EventFullDto event = getEventOrThrow(eventId);

        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Событие не принадлежит пользователю id=" + userId);
        }

        return requestRepository.findAllByEvent(eventId).stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public EventRequestStatusUpdateResult changeRequestsStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest statusUpdateRequest) {
        EventFullDto event = getEventOrThrow(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Событие не принадлежит пользователю id=" + userId);
        }

        List<ParticipationRequest> requests = requestRepository.findAllById(statusUpdateRequest.getRequestIds());

        for (ParticipationRequest request : requests) {
            if (request.getStatus() != RequestStatus.PENDING) {
                throw new ValidationException("Можно менять статус только у заявок в состоянии PENDING");
            }

            if (statusUpdateRequest.getStatus() == RequestStatus.CONFIRMED) {
                if (event.getParticipantLimit() != 0 &&
                        event.getConfirmedRequests() != null &&
                        event.getConfirmedRequests() >= event.getParticipantLimit()) {
                    throw new ParticipantLimitReachedException("Лимит участников уже достигнут");
                }
                request.setStatus(RequestStatus.CONFIRMED);
            } else {
                request.setStatus(statusUpdateRequest.getStatus());
            }
        }

        requestRepository.saveAll(requests);

        List<ParticipationRequestDto> confirmedRequests = requests.stream()
                .filter(r -> r.getStatus() == RequestStatus.CONFIRMED)
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());

        List<ParticipationRequestDto> rejectedRequests = requests.stream()
                .filter(r -> r.getStatus() == RequestStatus.REJECTED)
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());

        return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
    }

    public Long getConfirmedRequests(Long userId, Long eventId) {
        getUserOrThrow(userId);
        return requestRepository.countConfirmedRequestsByEventId(eventId);
    }

    private EventFullDto getEventOrThrow(Long eventId) {
        return eventServiceClient.getEventFullById(eventId)
                .orElseThrow(() -> {
                    log.error("Событие с id={} не найдено", eventId);
                    return new NotFoundException("Событие с id=" + eventId + " не найдено");
                });
    }

    private UserShortDto getUserOrThrow(Long id) {
        return userServiceClient.getUserById(id)
                .orElseThrow(() -> {
                    log.error("Пользователь с id={} не найден", id);
                    return new NotFoundException("Пользователь с id=" + id + " не найден");
                });
    }
}
