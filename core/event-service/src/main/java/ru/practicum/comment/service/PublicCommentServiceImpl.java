package ru.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import ru.practicum.dto.CommentShortDto;

import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.model.CommentMapper;
import ru.practicum.model.Comment;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PublicCommentServiceImpl implements PublicCommentService {
    private final CommentRepository commentRepository;

    @Override
    public List<CommentShortDto> getAllByEventId(long id) {
        List<Comment> comments = commentRepository.findAllByEventId(id);

        List<CommentShortDto> commentsDto = comments.stream()
                .map(CommentMapper::toCommentShortDto)
                .toList();

        log.info("получен список commentsDto для event с id = " + id);
        return commentsDto;
    }
}
