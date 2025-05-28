import ru.practicum.comment.model.Comment;
import ru.practicum.dto.CommentShortDto;
import ru.practicum.dto.NewComment;
import ru.practicum.dto.UserShortDto;

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

    public static CommentShortDto toCommentShortDto(Comment comment, UserShortDto author) {
        CommentShortDto commentShortDto = new CommentShortDto();
        commentShortDto.setId(comment.getId());
        commentShortDto.setEventId(comment.getEventId());
        commentShortDto.setAuthor(author);
        commentShortDto.setText(comment.getText());
        commentShortDto.setPublishedOn(comment.getPublishedOn());
        commentShortDto.setIsUpdated(comment.getIsUpdated());
        commentShortDto.setUpdatedOn(comment.getUpdatedOn());
        return commentShortDto;
    }

    public static List<CommentShortDto> toCommentShortDto(
            Iterable<Comment> comments,
            java.util.Map<Long, UserShortDto> authorsById
    ) {
        List<CommentShortDto> shortComments = new ArrayList<>();
        for (Comment comment : comments) {
            UserShortDto author = authorsById.get(comment.getAuthorId());
            shortComments.add(toCommentShortDto(comment, author));
        }
        return shortComments;
    }
}
