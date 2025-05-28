package ru.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.model.CommentMapper;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.dto.CommentShortDto;
import ru.practicum.dto.UserShortDto;
import ru.practicum.feign.UserServiceClient;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminCommentServiceImpl implements AdminCommentService {

    private final CommentRepository commentRepository;
    private final UserServiceClient userServiceClient;

    @Override
    public List<CommentShortDto> getCommentsByParams(List<Long> userIds, List<Long> eventIds, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Comment> comments = commentRepository.findByUserIdInAndEventIdIn(userIds, eventIds, pageable);

        List<Long> authorIds = comments.stream()
                .map(Comment::getAuthorId)
                .distinct()
                .collect(Collectors.toList());

        List<UserShortDto> authors = userServiceClient.getUsersWithIds(authorIds);


        Map<Long, UserShortDto> authorMap = authors.stream()
                .collect(Collectors.toMap(UserShortDto::getId, dto -> dto));

        return CommentMapper.toCommentShortDto(comments, authorMap);
    }

    @Override
    @Transactional
    public void deleteCommentById(Long id) {
        commentRepository.deleteById(id);
    }
}
