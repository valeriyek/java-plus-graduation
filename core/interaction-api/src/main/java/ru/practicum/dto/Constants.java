package ru.practicum.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
/**
 * Общие константы проекта.
 * <p>Класс утилитарный, не предназначен для инстанцирования.</p>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {
    /**
     * Формат даты/времени для сериализации и десериализации:
     * {@code yyyy-MM-dd HH:mm:ss}.
     * <p>Пример: {@code 2025-08-28 13:45:00}.</p>
     */
    public static final String FORMAT_DATETIME = "yyyy-MM-dd HH:mm:ss";

}
