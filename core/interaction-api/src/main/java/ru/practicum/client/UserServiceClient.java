package ru.practicum.client;

import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.dto.UserDto;


import java.util.Optional;


@FeignClient(name = "user-service", path = "/admin/users", contextId = "userServiceClient")
public interface UserServiceClient {

    @GetMapping("/{id}")
    Optional<UserDto> getUserById(@PathVariable Long id) throws FeignException;

}