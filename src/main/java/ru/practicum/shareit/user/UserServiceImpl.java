package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.Collection;
import java.util.Optional;


@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Autowired
    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public Collection<User> findAll() {
        log.info("Start User findAll()");
        return repository.findAll();
    }

    @Override
    public User create(User user) {
        log.info("US->Service User = " + user);
        if (checkExistsEmail(user, findAll())) {
            String str = "Этот e-mail уже есть у другого пользователя";
            log.error(str);
            throw new ConflictException(str);
        }
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("USer -> create(): user.name = {}",user.getName());
        }
        // сохраняем новую публикацию в памяти приложения
        User createdUser = repository.create(user);
        log.info("Новый пользователь сохранен S (id=" + createdUser.getId() + ", email='" + createdUser.getEmail() + "')");
        return createdUser;
    }

    @Override
    public User update(User newUser,long id) {
        // проверяем необходимые условия
        log.info("Start U-S update()");
        log.info("PATCH->Body User = " + newUser);
        if (id <= 0) {
            log.error("ValidationException id");
            throw new ValidationException("Id должен быть указан и значение больше 0");
        }
        newUser.setId(id);
        if (checkEmail(newUser, findAll())) { //
            log.error("ConflictException email");
            throw new ConflictException("Этот e-mail уже есть у другого пользователя");
        }
        User oldUser = findUserById(newUser.getId());

        if (newUser.getEmail() != null && !checkEmail(newUser, findAll())) {
            oldUser.setEmail(newUser.getEmail());
        }
        if (newUser.getName() != null) {
            oldUser.setName(newUser.getName());
        }
        // если user найдена и все условия соблюдены, обновляем её содержимое

        log.info("Данные пользователя обновляются (id=" + oldUser.getId() + ", email='" + oldUser.getEmail() + "')");
        return repository.update(oldUser);
    }

    private boolean checkEmail(User user, Collection<User> userCollection) {
        return userCollection
                .stream()
                .filter(u -> u.getEmail().equals(user.getEmail())) // Фильтруем по email
                .anyMatch(u -> !u.getId().equals(user.getId())); // Проверяем, что это не тот же самый пользователь
    }

    private boolean checkExistsEmail(User user, Collection<User> userCollection) {
        return userCollection
                .stream()
                .anyMatch(u -> u.getEmail().equals(user.getEmail())); // Проверяем, что это не тот же самый пользователь
    }

    @Override
    public User findUserById(long id) {
        Optional<User> findUser = repository.findUserById(id);
        if (findUser.isEmpty()) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        return findUser.get();
    }

    @Override
    public void delUserById(long id) {
        log.info("Start U-S delUserById({})", id);
        boolean delUser = repository.delUserById(id);
        if (!delUser) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        log.info("Удален User delUserById({})", id);
    }
}