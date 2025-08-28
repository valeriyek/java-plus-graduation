package ru.practicum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
/**
 * DTO категории событий.
 * <p>Используется в публичном и административном API для передачи данных категории.</p>
 *
 * <ul>
 *   <li>{@code id} — идентификатор категории (nullable при создании);</li>
 *   <li>{@code name} — название категории, обязательное, длина от 1 до 50 символов.</li>
 * </ul>
 */
@Data
public class CategoryDto {

    private Long id;

    @NotBlank(message = "Название категории не может быть пустым")
    @Size(min = 1, max = 50)
    private String name;
}
