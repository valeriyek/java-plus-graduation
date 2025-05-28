package ru.practicum.comment.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.*;
import lombok.*;



import java.time.LocalDateTime;

@Entity
@Table(name = "event_comments")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String text;

//    @ManyToOne
//    @JoinColumn(name = "author_id")
//    private User author;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
//    @JoinColumn(name = "event_id")
//    private Event event;

    @Column(name = "author_id", nullable = false)
    private Long authorId;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;

    @Column
    private Boolean isUpdated;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedOn;

}
