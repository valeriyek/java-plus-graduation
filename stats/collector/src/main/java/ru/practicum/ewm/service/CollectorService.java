package ru.practicum.ewm.service;

import ru.practicum.ewm.stats.messages.UserActionProto;

public interface CollectorService {
    void collectUserAction(UserActionProto request);
}