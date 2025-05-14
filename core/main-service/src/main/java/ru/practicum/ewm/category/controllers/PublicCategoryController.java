package ru.practicum.ewm.category.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.service.PublicCategoryService;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping("/categories")
public class PublicCategoryController {

    private final PublicCategoryService publicCategoryService;

    /** 1.  GET /categories?from={}&size={}  — страничный вывод */
    @GetMapping
    public List<CategoryDto> getAllCategories(
            @RequestParam(defaultValue = "0")
            @PositiveOrZero(message = "'from' не может быть отрицательным") int from,

            @RequestParam(defaultValue = "10")
            @Positive(message = "'size' должен быть больше 0") int size) {

        log.info("GET /categories  from={}, size={}", from, size);
        List<CategoryDto> result = publicCategoryService.getAllCategories(from, size);
        log.info(" -> {}", result);
        return result;
    }

    /** 2.  GET /categories/{catId}  — одна категория */
    @GetMapping("/{catId}")
    public CategoryDto getCategoryById(
            @PathVariable
            @Positive(message = "catId должен быть положительным") long catId) {

        log.info("GET /categories/{}", catId);
        CategoryDto result = publicCategoryService.getCategoryById(catId);
        log.info(" -> {}", result);
        return result;
    }
}
