package ru.practicum.ewm.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.dto.RequestStatus;
import ru.practicum.ewm.request.model.ParticipationRequest;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {

    // Все заявки конкретного пользователя
    List<ParticipationRequest> findAllByRequesterId(Long requesterId);

    // Все заявки на конкретное событие
    List<ParticipationRequest> findAllByEventId(Long eventId);

    // Метод для получения количества заявок с определенным eventId и статусом CONFIRMED
//    @Query("""
//            SELECT COUNT(pr)
//            FROM ParticipationRequest pr
//            WHERE pr.event.id = :eventId AND pr.status = 'CONFIRMED'
//            """)
//    Long countConfirmedRequestsByEventId(@Param("eventId") Long eventId);
    Long countByEventIdAndStatus(Long eventId, RequestStatus status);

    boolean existsByRequesterIdAndEventId(Long requesterId, Long eventId);

    Optional<ParticipationRequest> findByIdAndRequesterId(Long requestId, Long requesterId);
}
