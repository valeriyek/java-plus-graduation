package ru.practicum.request.repository;

import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.dto.RequestStatus;
import ru.practicum.model.ParticipationRequest;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {

    List<ParticipationRequest> findAllByRequesterId(Long requesterId);

    List<ParticipationRequest> findAllByEventId(Long eventId);
   @Query("""
            SELECT COUNT(pr)
            FROM ParticipationRequest pr
            WHERE pr.event.id = :eventId AND pr.status = 'CONFIRMED'
            """)
    Long countConfirmedRequestsByEventId(@Param("eventId") Long eventId);

    boolean existsByRequesterIdAndEventId(Long requesterId, Long eventId);

    Optional<ParticipationRequest> findByIdAndRequesterId(Long requestId, Long requesterId);

    @Query("SELECT e.views FROM Event e WHERE e.id = :eventId")
    Long getViewsForEvent(@Param("eventId") Long eventId);}
