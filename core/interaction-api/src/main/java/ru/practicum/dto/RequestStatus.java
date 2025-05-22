package ru.practicum.dto;

public enum RequestStatus {
    PENDING,     // Ожидает подтверждения
    CONFIRMED,   // Подтверждена
    REJECTED,    // Отклонена
    CANCELED     // Отменена пользователем
}
