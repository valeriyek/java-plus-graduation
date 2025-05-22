package ru.practicum.request.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.ParticipationRequest;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {


    List<ParticipationRequest> findAllByRequester(Long requesterId);

    List<ParticipationRequest> findAllByEvent(Long eventId);

    @Query("""
            SELECT COUNT(pr)
            FROM ParticipationRequest pr
            WHERE pr.event = :eventId AND pr.status = 'CONFIRMED'
            """)
    Long countConfirmedRequestsByEventId(@Param("eventId") Long eventId);

    boolean existsByRequesterAndEvent(Long requesterId, Long eventId);

    Optional<ParticipationRequest> findByIdAndRequester(Long requestId, Long requesterId);

    @Query("SELECT e.views FROM Event e WHERE e.id = :eventId")
    Long getViewsForEvent(@Param("eventId") Long eventId);
}
