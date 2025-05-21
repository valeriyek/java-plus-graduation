package ru.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.client.UserServiceClient;
import ru.practicum.ewm.dto.CommentShortDto;
import ru.practicum.ewm.dto.NewComment;
import ru.practicum.ewm.dto.UpdateCommentDto;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.dto.UserShortDto;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.ForbiddenException;
import ru.practicum.ewm.exception.NotFoundException;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PrivateCommentServiceImpl implements PrivateCommentService {

    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserServiceClient userClient;

    @Override
    @Transactional
    public CommentShortDto createComment(Long userId, Long eventId, NewComment newComment) {
        UserDto user = checkUserExist(userId);
        Event event = checkEventExist(eventId);

        Comment comment = CommentMapper.fromNewCommentToComment(newComment, userId, event);
        Comment saved = commentRepository.save(comment);

        UserShortDto author = new UserShortDto();
        author.setId(user.getId());
        author.setName(user.getName());

        return CommentMapper.toCommentShortDto(saved, author);
    }

    @Override
    public List<CommentShortDto> getUserComments(Long userId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);

        List<Comment> comments = commentRepository.findByAuthorId(userId, pageable);
        if (comments.isEmpty()) return List.of();

        UserDto user = checkUserExist(userId);
        UserShortDto author = new UserShortDto();
        author.setId(user.getId());
        author.setName(user.getName());

        return comments.stream()
                .map(comment -> CommentMapper.toCommentShortDto(comment, author))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentShortDto updateComment(Long userId, Long commentId, UpdateCommentDto updateComment) {
        Comment comment = checkCommentExistAndAuthor(commentId, userId);

        comment.setText(updateComment.getText());
        comment.setIsUpdated(true);
        comment.setUpdatedOn(LocalDateTime.now());

        Comment updated = commentRepository.save(comment);

        UserDto user = checkUserExist(userId);
        UserShortDto author = new UserShortDto();
        author.setId(user.getId());
        author.setName(user.getName());

        return CommentMapper.toCommentShortDto(updated, author);
    }

    @Override
    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        Comment comment = checkCommentExistAndAuthor(commentId, userId);
        commentRepository.delete(comment);
    }

    private Event checkEventExist(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("События с id = " + id + " не существует"));
    }

    private UserDto checkUserExist(Long id) {
        return userClient.getUserById(id);
              //  .orElseThrow(() -> new NotFoundException("Пользователя с id = " + id + " не существует"));
    }

    private Comment checkCommentExistAndAuthor(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new NotFoundException("Комментарий с id = " + commentId + " не найден"));

        if (!comment.getAuthorId().equals(userId)) {
            throw new ForbiddenException("Пользователь не является автором комментария");
        }
        return comment;
    }
}
