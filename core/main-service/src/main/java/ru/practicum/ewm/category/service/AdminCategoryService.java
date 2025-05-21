package ru.practicum.ewm.category.service;

import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.dto.NewCategoryDto;

public interface AdminCategoryService {

    CategoryDto createCategory(NewCategoryDto newCategoryDto);

    CategoryDto updateCategory(CategoryDto categoryDto, Long id);

    void deleteCategoryById(Long id);

}
