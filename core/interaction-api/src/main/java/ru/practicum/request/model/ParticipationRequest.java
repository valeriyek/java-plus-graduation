package ru.practicum.request.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import ru.practicum.dto.RequestStatus;



import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ParticipationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Дата и время создания заявки
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;

    // Событие
    @Column(name = "event_id")
    private Long event;

    // Пользователь, отправивший заявку
    @Column(name = "requester_id")
    private Long requester;

    // Текущий статус заявки
    @Enumerated(EnumType.STRING)
    private RequestStatus status;
}