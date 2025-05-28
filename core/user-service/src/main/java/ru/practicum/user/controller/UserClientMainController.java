package ru.practicum.user.controller;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.practicum.dto.UserShortDto;
import ru.practicum.feign.UserServiceMainClient;

import ru.practicum.user.model.User;
import ru.practicum.user.model.UserMapper;
import ru.practicum.user.service.AdminUserService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserClientMainController implements UserServiceMainClient {
    private final AdminUserService adminUserService;


    @Override
    public Optional<UserShortDto> getUserById(Long id) throws FeignException {
        log.info("Поступил запрос Get /api/v1/user/{} на получение User с id = {}", id, id);
        Optional<User> user = adminUserService.getUserById(id);
        Optional<UserShortDto> result = user.map(UserMapper::toUserShortDto);
        log.info("Сформирован ответ Get /api/v1/user/{} с телом: {}", id, result);
        return result;
    }

    @Override
    public List<UserShortDto> getUsersWithIds(List<Long> ids) throws FeignException {
        return adminUserService.getUsersWithIds(ids).stream()
                .map(UserMapper::toUserShortDto)
                .collect(Collectors.toList());
    }

}