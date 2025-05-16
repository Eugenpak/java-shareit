package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public List<UserDto> findAll() {
        log.info("U-S User findAll()");
        List<User> list = findAllUser();
        return UserMapper.toDtos(list);
    }

    private List<User> findAllUser() {
        return repository.findAll().stream().toList();
    }

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        log.info("U-S -> Service UserDto = " + userDto);
        if (checkExistsEmail(userDto, findAllUser())) {
            String str = "Этот e-mail уже есть у другого пользователя";
            log.error(str);
            throw new ConflictException(str);
        }
        if (userDto.getName() == null || userDto.getName().isBlank()) {
            log.info("USer -> create(): user.name = {}",userDto.getName());
        }
        // сохраняем новую публикацию в памяти приложения
        User createdUser = repository.save(UserMapper.fromDto(userDto));
        log.info("Новый пользователь сохранен S (id=" + createdUser.getId() + ", email='" + createdUser.getEmail() + "')");
        return UserMapper.toDto(createdUser);
    }

    @Override
    @Transactional
    public UserDto update(UserDto newUserDto,long id) {
        // проверяем необходимые условия
        log.info("U-S Start update()");
        log.info("PATCH->Body UserDto = " + newUserDto);
        if (id <= 0) {
            log.error("ValidationException id");
            throw new ValidationException("Id должен быть указан и значение больше 0");
        }
        newUserDto.setId(id);
        if (checkEmail(newUserDto, findAllUser())) { //
            log.error("ConflictException email");
            throw new ConflictException("Этот e-mail уже есть у другого пользователя");
        }
        User oldUser = findById(id);
        User newUser = UserMapper.fromDto(newUserDto);

        if (newUser.getEmail() != null) {
            oldUser.setEmail(newUser.getEmail());
        }
        if (newUser.getName() != null) {
            oldUser.setName(newUser.getName());
        }
        // если user найдена и все условия соблюдены, обновляем её содержимое
        log.info("Данные пользователя обновляются (id=" + oldUser.getId() + ", email='" + oldUser.getEmail() + "')");

        User userUpdate = repository.save(oldUser);
        return UserMapper.toDto(userUpdate);
    }

    private boolean checkEmail(UserDto userDto, Collection<User> userCollection) {
        return userCollection
                .stream()
                .filter(u -> u.getEmail().equals(userDto.getEmail())) // Фильтруем по email
                .anyMatch(u -> !u.getId().equals(userDto.getId())); // Проверяем, что это не тот же самый пользователь
    }

    private boolean checkExistsEmail(UserDto userDto, Collection<User> userCollection) {
        return userCollection
                .stream()
                .anyMatch(u -> u.getEmail().equals(userDto.getEmail())); // Проверяем, что это не тот же самый пользователь
    }

    @Override
    public UserDto findUserById(long id) {
        log.info("U-S findUserById({})", id);
        return UserMapper.toDto(findById(id));
    }

    private User findById(long id) {
        Optional<User> findUser = repository.findById(id);
        if (findUser.isEmpty()) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        return findUser.get();
    }

    @Override
    @Transactional
    public void delUserById(long id) {
        log.info("U-S Start delUserById({})", id);

        findById(id);
        repository.deleteById(id);
        log.info("Удален User delUserById({})", id);
    }
}