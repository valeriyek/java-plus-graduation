package ru.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.client.UserServiceClient;
import ru.practicum.ewm.dto.CommentShortDto;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.dto.UserShortDto;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PublicCommentServiceImpl implements PublicCommentService {
    private final CommentRepository commentRepository;
    private final UserServiceClient userClient;

    @Override
    public List<CommentShortDto> getAllByEventId(long eventId) {
        List<Comment> comments = commentRepository.findAllByEventId(eventId);

        List<CommentShortDto> commentsDto = comments.stream()
                .map(comment -> {
                    UserDto userDto = userClient.getUserById(comment.getAuthorId());
                    UserShortDto shortDto = new UserShortDto();
                    shortDto.setId(userDto.getId());
                    shortDto.setName(userDto.getName());

                    return CommentMapper.toCommentShortDto(comment, shortDto);
                })
                .toList();

        log.info("получен список commentsDto для event с id = {}", eventId);
        return commentsDto;
    }
}
