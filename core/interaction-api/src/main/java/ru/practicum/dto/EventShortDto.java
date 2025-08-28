package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
/**
 * Краткий DTO события.
 * <p>Расширяет {@link EventBaseDto} минимальным набором данных
 * для отображения списка событий в публичном API.</p>
 *
 * <ul>
 *   <li>{@code eventDate} — дата и время проведения события
 *       (формат {@link Constants#FORMAT_DATETIME}).</li>
 * </ul>
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class EventShortDto extends EventBaseDto {
    private String eventDate;
}
