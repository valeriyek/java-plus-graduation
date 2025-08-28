package ru.practicum.dto;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
/**
 * Статусы жизненного цикла события.
 * <ul>
 *   <li>{@code PENDING} — ожидает модерации;</li>
 *   <li>{@code PUBLISHED} — опубликовано и доступно в публичном API;</li>
 *   <li>{@code CANCELED} — отменено инициатором или администратором.</li>
 * </ul>
 */
public enum EventState {
    PENDING,
    PUBLISHED,
    CANCELED;
    /**
     * Преобразует список строк в множество {@link EventState}.
     * <p>Сопоставление выполняется без учёта регистра.</p>
     * <p>Неверные значения конвертируются в {@code null} и всё равно попадают в Set.</p>
     *
     * @param states список строковых представлений статусов
     * @return множество состояний (может содержать {@code null})
     */
    public static Set<EventState> from(List<String> states) {
        return states.stream()
                .map(state -> {
                    for (EventState value : EventState.values()) {
                        if (value.name().equalsIgnoreCase(state)) {
                            return value;
                        }
                    }
                    return null;
                }).collect(Collectors.toSet());
    }

}
