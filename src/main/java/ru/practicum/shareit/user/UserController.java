package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
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
    public Collection<User> findAll() {
        log.info("Start User findAll()");
        return userService.findAll();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Start User create()");
        log.info("POST->Body User = " + user);
        // сохраняем новую публикацию в памяти приложения
        log.info("UC Новый пользователь сохраняется (id=" + user.getId() + ", email='" + user.getEmail() + "')");
        return userService.create(user);
    }

    @PatchMapping(value = "/{id}")
    public User update(@RequestBody User user,@NotNull @PathVariable long id) {
        // проверяем необходимые условия
        log.info("Start User update()");
        log.info("PUT->Body User = " + user);
        return userService.update(user,id);
    }

    @GetMapping(value = "/{id}")
    public User findUserById(@NotNull @PathVariable long id) {
        log.info("UC User findUserById(id=" + id + ")");
        User findUser = userService.findUserById(id);
        log.info("UC findUser: " + findUser);
        return findUser;
    }

    @DeleteMapping(value = "/{id}")
    public void delUserById(@NotNull @PathVariable long id) {
        log.info("UC User delUserById(id=" + id + ")");
        userService.delUserById(id);
        log.info("UC delUserById(id=" + id + ") удален!");
    }
}
