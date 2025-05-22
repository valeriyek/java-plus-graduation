package ru.practicum.user.controller;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.practicum.client.UserServiceMainClient;
import ru.practicum.model.User;
import ru.practicum.user.service.AdminUserService;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserClientMainController implements UserServiceMainClient {
    private final AdminUserService adminUserService;

    @Override
    public Optional<User> getUserById(Long id) throws FeignException {
        log.info("Поступил запрос Get /api/v1/user/{} на получение User с id = {}", id, id);
        Optional<User> response = adminUserService.getUserById(id);
        log.info("Сформирован ответ Get /api/v1/user/{} с телом: {}", id, response);
        return response;
    }

    @Override
    public List<User> getUsersWithIds(List<Long> ids) throws FeignException {
        return adminUserService.getUsersWithIds(ids);
    }
}