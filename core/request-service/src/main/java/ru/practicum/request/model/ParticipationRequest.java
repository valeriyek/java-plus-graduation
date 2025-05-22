package ru.practicum.request.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import ru.practicum.dto.RequestStatus;



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
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
//    @JoinColumn(name = "event_id")
//    private Event event;
    @Column(name = "event_id")
    private Long eventId;


//    @ManyToOne(fetch = FetchType.LAZY)
//    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
//    @JoinColumn(name = "requester_id")
//    private User requester;
@Column(name = "requester_id")
private Long requesterId;

    // Текущий статус заявки
    @Enumerated(EnumType.STRING)
    private RequestStatus status;
}
