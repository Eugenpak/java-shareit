package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@Validated
@Slf4j
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDto> findAll() {
        log.info("Start User findAll()");
        return userService.findAll();
    }

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        log.info("Start UserDto create()");
        log.info("POST->Body UserDto = " + userDto);
        // сохраняем новую публикацию в памяти приложения
        log.info("UC Новый пользователь сохраняется (id=" + userDto.getId() + ", email='" + userDto.getEmail() + "')");
        return userService.create(userDto);
    }

    @PatchMapping(value = "/{id}")
    public UserDto update(@RequestBody UserDto userDto,@NotNull @PathVariable long id) {
        // проверяем необходимые условия
        log.info("Start UserDto update()");
        log.info("PUT->Body UserDto = " + userDto);
        return userService.update(userDto,id);
    }

    @GetMapping(value = "/{id}")
    public UserDto findUserById(@NotNull @PathVariable long id) {
        log.info("UC UserDto findUserById(id=" + id + ")");
        UserDto findUser = userService.findUserById(id);
        log.info("UC findUser: " + findUser);
        return findUser;
    }

    @DeleteMapping(value = "/{id}")
    public void delUserById(@NotNull @PathVariable long id) {
        log.info("UC UserDto delUserById(id=" + id + ")");
        userService.delUserById(id);
        log.info("UC delUserById(id=" + id + ") удален!");
    }
}
