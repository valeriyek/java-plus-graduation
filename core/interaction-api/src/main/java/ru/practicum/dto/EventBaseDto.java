package ru.practicum.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * Базовый DTO события.
 * <p>Используется как родительская структура для публичного и административного API.</p>
 *
 * <ul>
 *   <li>{@code annotation} — краткое описание события;</li>
 *   <li>{@code category} — категория события;</li>
 *   <li>{@code confirmedRequests} — число подтверждённых заявок на участие;</li>
 *   <li>{@code id} — идентификатор события;</li>
 *   <li>{@code initiator} — инициатор (создатель) события;</li>
 *   <li>{@code paid} — признак платного события;</li>
 *   <li>{@code title} — заголовок события;</li>
 *   <li>{@code rating} — рейтинг события;</li>
 *   <li>{@code commentsCount} — количество комментариев.</li>
 * </ul>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventBaseDto {
    private String annotation;
    private CategoryDto category;
    private Long confirmedRequests;
    private Long id;
    private UserShortDto initiator;
    private Boolean paid;
    private String title;
    private Double rating;
    private Long commentsCount;
}
