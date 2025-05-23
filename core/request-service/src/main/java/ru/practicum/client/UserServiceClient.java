package ru.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.practicum.feign.UserServiceMainClient;

@FeignClient(name = "user-service", path = "/api/v1/user", contextId = "userServiceClient")
public interface UserServiceClient extends UserServiceMainClient {

}