package ru.practicum.shareit.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "users")
@Getter @Setter @ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //— уникальный идентификатор пользователя
    @NotBlank(message = "must not be blank")
    @Email(message = "Недопустимый email. Попробуйте снова.")
    @Column(name = "email", nullable = false, unique = true)
    private String email; //— электронная почта — email;
    @Column(name = "name", nullable = false)
    private String name; //— имя для отображения — name;
}
