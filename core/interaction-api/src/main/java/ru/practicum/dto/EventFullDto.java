package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
/**
 * Полный DTO события.
 * <p>Расширяет {@link EventBaseDto} дополнительными полями, используемыми
 * в административном и публичном API для детального просмотра.</p>
 *
 * <ul>
 *   <li>{@code createdOn} — дата и время создания события (формат {@link Constants#FORMAT_DATETIME});</li>
 *   <li>{@code description} — полное текстовое описание;</li>
 *   <li>{@code eventDate} — дата и время проведения события;</li>
 *   <li>{@code location} — координаты проведения;</li>
 *   <li>{@code participantLimit} — ограничение на число участников (0 = без ограничений);</li>
 *   <li>{@code publishedOn} — дата и время публикации;</li>
 *   <li>{@code requestModeration} — признак необходимости модерации заявок;</li>
 *   <li>{@code state} — текущее состояние события (например, PENDING, PUBLISHED, CANCELED).</li>
 * </ul>
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class EventFullDto extends EventBaseDto {
    private String createdOn;
    private String description;
    private String eventDate;
    private LocationDto location;
    private Long participantLimit;
    private String publishedOn;
    private Boolean requestModeration;
    private String state;
}