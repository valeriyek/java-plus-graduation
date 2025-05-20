package ru.practicum.ewm.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
@Transactional(readOnly = true)
public class AdminCommentServiceImpl implements AdminCommentService {

    private final CommentRepository commentRepository;
    private final UserServiceClient userClient;

    @Override
    public List<CommentShortDto> getCommentsByParams(List<Long> userIds, List<Long> eventIds, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);

        List<Comment> comments = commentRepository.findByUserIdInAndEventIdIn(userIds, eventIds, pageable);

        return comments.stream()
                .map(comment -> {
                    UserDto userDto = userClient.getUserById(comment.getAuthorId());
                    UserShortDto shortDto = new UserShortDto();
                    shortDto.setId(userDto.getId());
                    shortDto.setName(userDto.getName());

                    return CommentMapper.toCommentShortDto(comment, shortDto);
                })
                .toList();
    }

    @Override
    @Transactional
    public void deleteCommentById(Long id) {
        commentRepository.deleteById(id);
    }

}
