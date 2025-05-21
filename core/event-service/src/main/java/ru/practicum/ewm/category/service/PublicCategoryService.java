package ru.practicum.ewm.category.service;

import ru.practicum.ewm.dto.CategoryDto;

import java.util.List;

public interface PublicCategoryService {
    List<CategoryDto> getAllCategories(int from, int size);

    CategoryDto getCategoryById(long id);
}
