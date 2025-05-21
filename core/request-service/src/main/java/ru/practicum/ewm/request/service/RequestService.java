package ru.practicum.ewm.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.client.EventServiceClient;
import ru.practicum.ewm.client.UserServiceClient;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ParticipantLimitReachedException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.request.model.ParticipationRequest;
import ru.practicum.ewm.request.repository.RequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestService {

    private final RequestRepository requestRepository;
    private final EventServiceClient eventClient;
    private final UserServiceClient userClient;

    public List<ParticipationRequestDto> getRequestsOfUser(Long userId) {
        checkUserExists(userId);
        return requestRepository.findAllByRequesterId(userId).stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ParticipationRequestDto addRequest(Long userId, Long eventId) {
        checkUserExists(userId);
        EventFullDto event = getEventOrThrow(eventId);

        if (event.getInitiator().getId().equals(userId)) {
            throw new ValidationException("Инициатор события не может подать заявку на участие.");
        }
        if (event.getState() != EventState.PUBLISHED) {
            throw new ValidationException("Событие ещё не опубликовано.");
        }
        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new ValidationException("Заявка уже подана.");
        }
        if (event.getParticipantLimit() != 0 && event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new ParticipantLimitReachedException("Лимит участников достигнут.");
        }

        ParticipationRequest request = new ParticipationRequest();
        request.setCreated(LocalDateTime.now());
        request.setEventId(eventId);
        request.setRequesterId(userId);

        boolean autoConfirm = !event.getRequestModeration() || event.getParticipantLimit() == 0;
        request.setStatus(autoConfirm ? RequestStatus.CONFIRMED : RequestStatus.PENDING);

        log.info("Создание заявки: moderation={}, limit={}, autoConfirm={}",
                event.getRequestModeration(), event.getParticipantLimit(), autoConfirm);

        ParticipationRequest saved = requestRepository.save(request);

        if (RequestStatus.CONFIRMED.equals(saved.getStatus())) {
            updateConfirmedRequests(eventId);
        }

        return RequestMapper.toParticipationRequestDto(saved);
    }

    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        ParticipationRequest request = requestRepository.findByIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> new NotFoundException("Заявка не найдена или не принадлежит пользователю."));

        boolean wasConfirmed = RequestStatus.CONFIRMED.equals(request.getStatus());
        request.setStatus(RequestStatus.CANCELED);
        ParticipationRequest updated = requestRepository.save(request);

        if (wasConfirmed) {
            updateConfirmedRequests(request.getEventId());
        }

        return RequestMapper.toParticipationRequestDto(updated);
    }

    public List<ParticipationRequestDto> getRequestsForUserEvent(Long userId, Long eventId) {
        checkUserExists(userId);
        EventFullDto event = getEventOrThrow(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Событие не принадлежит пользователю.");
        }

        return requestRepository.findAllByEventId(eventId).stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public EventRequestStatusUpdateResult changeRequestsStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest updateRequest) {
        EventFullDto event = getEventOrThrow(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Событие не принадлежит пользователю.");
        }

        List<ParticipationRequest> requests = requestRepository.findAllById(updateRequest.getRequestIds());

        for (ParticipationRequest request : requests) {
            if (request.getStatus() != RequestStatus.PENDING) {
                throw new ValidationException("Можно менять статус только у заявок в состоянии PENDING");
            }

            if (updateRequest.getStatus() == RequestStatus.CONFIRMED) {
                if (event.getParticipantLimit() != 0 && event.getConfirmedRequests() >= event.getParticipantLimit()) {
                    throw new ParticipantLimitReachedException("Лимит участников достигнут");
                }
                request.setStatus(RequestStatus.CONFIRMED);
            } else {
                request.setStatus(updateRequest.getStatus());
            }
        }

        requestRepository.saveAll(requests);
        updateConfirmedRequests(eventId);

        List<ParticipationRequestDto> confirmed = requests.stream()
                .filter(r -> r.getStatus() == RequestStatus.CONFIRMED)
                .map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList());

        List<ParticipationRequestDto> rejected = requests.stream()
                .filter(r -> r.getStatus() == RequestStatus.REJECTED)
                .map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList());

        return new EventRequestStatusUpdateResult(confirmed, rejected);
    }

    @Transactional
    public void updateConfirmedRequests(Long eventId) {
        Long confirmed = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        confirmed = confirmed == null ? 0L : confirmed;
        eventClient.updateConfirmedRequests(eventId, confirmed);
    }

    private void checkUserExists(Long userId) {
        userClient.getUserById(userId);
    }

    private EventFullDto getEventOrThrow(Long eventId) {
        return eventClient.getEventById(eventId);
    }
}
