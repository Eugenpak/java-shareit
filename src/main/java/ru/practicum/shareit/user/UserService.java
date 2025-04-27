package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import java.util.List;

public interface UserService {
    void delUserById(long id);

    UserDto findUserById(long id);

    UserDto update(UserDto newUserDto,long id);

    UserDto create(UserDto userDto);

    List<UserDto> findAll();
}
