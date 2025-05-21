package ru.practicum.comment.service;

import ru.practicum.ewm.dto.CommentShortDto;

import java.util.List;

public interface PublicCommentService {
    List<CommentShortDto> getAllByEventId(long id);
}
