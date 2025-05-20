package ru.practicum.ewm.feign;

import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.model.User;


import java.util.Optional;

@FeignClient(name = "user-service", path = "/admin/users")
public interface UserServiceClient {

    @GetMapping("/{id}")
    UserDto getUserById(@PathVariable Long id);

}