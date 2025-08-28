package ru.practicum.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO пользователя.
 * <p>Используется в административном и публичном API для передачи данных о пользователях.</p>
 *
 * <ul>
 *   <li>{@code id} — идентификатор пользователя;</li>
 *   <li>{@code name} — имя пользователя, обязательное, длиной от 2 до 250 символов;</li>
 *   <li>{@code email} — адрес электронной почты, обязательный, длиной от 6 до 254 символов,
 *       должен соответствовать формату {@code local@domain}.</li>
 * </ul>
 */
@Data
@AllArgsConstructor
public class UserDto {

    private Long id;

    @NotBlank(message = "Имя не может быть пустым")
    @Size(min = 2, max = 250)
    private String name;

    @NotBlank(message = "Email не может быть пустым")
    @Size(min = 6, max = 254)
    @Email(regexp = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$", message = "Некорректный email адрес")
    private String email;

}
