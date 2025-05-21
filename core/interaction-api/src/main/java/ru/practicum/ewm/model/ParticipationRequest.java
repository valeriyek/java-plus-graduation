package ru.practicum.ewm.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import ru.practicum.ewm.dto.RequestStatus;


import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Data
public class ParticipationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Дата и время создания заявки
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;

    // Событие
    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "requester_id", nullable = false)
    private Long requesterId;


    // Текущий статус заявки
    @Enumerated(EnumType.STRING)
    private RequestStatus status;
}
