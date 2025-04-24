package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TODO Sprint add-controllers.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id; //— уникальный идентификатор пользователя
    @NotBlank(message = "must not be blank")
    @Email(message = "Недопустимый email. Попробуйте снова.")
    private String email; //— электронная почта — email;
    private String name; //— имя для отображения — name;
}
