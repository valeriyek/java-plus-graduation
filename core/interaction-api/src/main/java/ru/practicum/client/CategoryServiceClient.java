package ru.practicum.client;

import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CategoryDto;


import java.util.Optional;

@FeignClient(name = "category-service", path = "/categories", contextId = "categoryServiceClient")
public interface CategoryServiceClient {

    @GetMapping("/{catId}/full")
    Optional<CategoryDto> getFullCategoriesById(@PathVariable long catId) throws FeignException;

}