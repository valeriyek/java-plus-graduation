package ru.practicum.ewm.dto;

public enum RequestStatus {
    PENDING,     // Ожидает подтверждения
    CONFIRMED,   // Подтверждена
    REJECTED,    // Отклонена
    CANCELED     // Отменена пользователем
}
