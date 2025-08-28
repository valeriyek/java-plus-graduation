package ru.practicum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Краткий DTO пользователя.
 * <p>Используется в событиях и заявках, когда требуется только идентификатор и имя.</p>
 *
 * <ul>
 *   <li>{@code id} — идентификатор пользователя;</li>
 *   <li>{@code name} — имя пользователя, обязательное, длиной до 256 символов.</li>
 * </ul>
 */
@Data
@AllArgsConstructor
public class UserShortDto {

    private Long id;

    @NotBlank(message = "Имя не может быть пустым")
    @Size(max = 256)
    private String name;

}
