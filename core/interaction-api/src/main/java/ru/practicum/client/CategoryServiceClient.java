package ru.practicum.client;

import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.Category;


import java.util.Optional;

@FeignClient(name = "category-service", path = "/categories", contextId = "categoryServiceClient")
public interface CategoryServiceClient {

    @GetMapping("/{catId}/full")
    Optional<Category> getFullCategoriesById(@PathVariable long catId) throws FeignException;

}