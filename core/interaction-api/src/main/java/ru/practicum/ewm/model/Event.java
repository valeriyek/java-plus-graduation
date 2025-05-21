package ru.practicum.ewm.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.dto.EventState;
import ru.practicum.ewm.dto.Location;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Data
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    private String annotation;

    @Column(nullable = false)
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;


    @Embedded
    private Location location;


    private boolean paid;

    @Column(nullable = false)
    private int participantLimit;

    private boolean requestModeration;

    @Enumerated(EnumType.STRING)
    private EventState state;

    @Column(nullable = false)
    private String title;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;

    @Column(name = "initiator_id", nullable = false)
    private Long initiatorId;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "views", nullable = false)
    private Long views = 0L;

    @Column(name = "confirmed_requests", nullable = false)
    private Long confirmedRequests = 0L;
}
