package ru.practicum.comment.model;


import ru.practicum.dto.CommentShortDto;
import ru.practicum.dto.NewComment;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CommentMapper {

    public static Comment fromNewCommentToComment(NewComment newComment, Long authorId, Long eventId) {
        Comment comment = new Comment();
        comment.setText(newComment.getText());
        comment.setAuthorId(authorId);
        comment.setEventId(eventId);
        comment.setPublishedOn(LocalDateTime.now());
        comment.setIsUpdated(false);
        return comment;
    }


    public static CommentShortDto toCommentShortDto(Comment comment) {
        CommentShortDto dto = new CommentShortDto();
        dto.setId(comment.getId());
        dto.setEventId(comment.getEventId());
        dto.setAuthorId(comment.getAuthorId()); // только id, без UserShortDto
        dto.setText(comment.getText());
        dto.setPublishedOn(comment.getPublishedOn());
        dto.setIsUpdated(comment.getIsUpdated());
        dto.setUpdatedOn(comment.getUpdatedOn());
        return dto;
    }


    public static List<CommentShortDto> toCommentShortDto(Iterable<Comment> comments) {
        List<CommentShortDto> shortComments = new ArrayList<>();
        for (Comment comment : comments) {
            shortComments.add(toCommentShortDto(comment));
        }
        return shortComments;
    }

}
