package ru.practicum.ewm.comment.service;

import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.dto.CommentShortDto;
import ru.practicum.ewm.dto.NewComment;
import ru.practicum.ewm.dto.UserShortDto;
import ru.practicum.ewm.event.model.Event;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommentMapper {

    public static Comment fromNewCommentToComment(NewComment newComment, Long userId, Event event) {
        Comment comment = new Comment();
        comment.setText(newComment.getText());
        comment.setAuthorId(userId);
        comment.setEvent(event);
        comment.setPublishedOn(LocalDateTime.now());
        comment.setIsUpdated(false);
        return comment;
    }

    public static CommentShortDto toCommentShortDto(Comment comment, UserShortDto author) {
        CommentShortDto commentShortDto = new CommentShortDto();
        commentShortDto.setId(comment.getId());
        commentShortDto.setEventId(comment.getEvent().getId());
        commentShortDto.setAuthor(author);
        commentShortDto.setText(comment.getText());
        commentShortDto.setPublishedOn(comment.getPublishedOn());
        commentShortDto.setIsUpdated(comment.getIsUpdated());
        commentShortDto.setUpdatedOn(comment.getUpdatedOn());
        return commentShortDto;
    }

    public static List<CommentShortDto> toCommentShortDto(Iterable<Comment> comments, Map<Long, UserShortDto> authorsById) {
        List<CommentShortDto> result = new ArrayList<>();
        for (Comment comment : comments) {
            UserShortDto author = authorsById.getOrDefault(comment.getAuthorId(), null);
            result.add(toCommentShortDto(comment, author));
        }
        return result;
    }

}
