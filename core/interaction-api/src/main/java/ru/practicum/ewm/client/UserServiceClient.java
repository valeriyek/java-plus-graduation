package ru.practicum.ewm.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.ewm.dto.UserDto;


@FeignClient(name = "user-service", path = "/admin/users")
public interface UserServiceClient {

    @GetMapping("/{id}")
    UserDto getUserById(@PathVariable Long id);

}