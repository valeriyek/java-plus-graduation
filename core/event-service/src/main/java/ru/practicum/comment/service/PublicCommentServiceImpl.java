package ru.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import ru.practicum.comment.model.Comment;
import ru.practicum.comment.model.CommentMapper;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.dto.CommentShortDto;
import ru.practicum.dto.UserShortDto;
import ru.practicum.feign.UserServiceClient;

import java.util.List;
import java.util.Map;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PublicCommentServiceImpl implements PublicCommentService {

    private final CommentRepository commentRepository;
    private final UserServiceClient userServiceClient;
    private final CommentMapper commentMapper;

    @Override
    public List<CommentShortDto> getAllByEventId(long id) {
        List<Comment> comments = commentRepository.findAllByEventId(id);

        List<Long> authorIds = comments.stream()
                .map(Comment::getAuthorId)
                .distinct()
                .collect(Collectors.toList());

        List<UserShortDto> users = userServiceClient.getUsersWithIds(authorIds);


        Map<Long, UserShortDto> userMap = users.stream()
                .collect(Collectors.toMap(UserShortDto::getId, dto -> dto));

        List<CommentShortDto> commentsDto = commentMapper.toCommentShortDto(comments, userMap);

        log.info("Получен список commentsDto для event с id = {}", id);
        return commentsDto;
    }
}
