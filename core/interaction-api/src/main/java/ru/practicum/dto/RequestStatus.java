package ru.practicum.dto;
/**
 * Статусы заявок на участие в событиях.
 * <ul>
 *   <li>{@code PENDING} — заявка ожидает подтверждения;</li>
 *   <li>{@code CONFIRMED} — заявка подтверждена и пользователь включён в список участников;</li>
 *   <li>{@code REJECTED} — заявка отклонена организатором или администратором;</li>
 *   <li>{@code CANCELED} — заявка отменена пользователем.</li>
 * </ul>
 */
public enum RequestStatus {
    PENDING,
    CONFIRMED,
    REJECTED,
    CANCELED
}