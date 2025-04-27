package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id; //— уникальный идентификатор пользователя
    @NotBlank(message = "must not be blank")
    @Email(message = "Недопустимый email. Попробуйте снова.")
    private String email; //— электронная почта — email;
    private String name; //— имя для отображения — name;
}
