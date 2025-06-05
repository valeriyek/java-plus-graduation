package ru.practicum.ewm.handler;

import ru.practicum.ewm.stats.avro.UserActionAvro;

public interface UserActionHandler {
    void handleUserAction(UserActionAvro userActionAvro);
}