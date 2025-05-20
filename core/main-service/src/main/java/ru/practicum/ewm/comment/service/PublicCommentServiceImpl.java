package ru.practicum.ewm.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.comment.dto.CommentShortDto;
import ru.practicum.ewm.comment.dto.mapper.CommentMapper;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.comment.repository.CommentRepository;
import ru.practicum.ewm.feign.UserServiceClient;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.dto.UserShortDto;

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
