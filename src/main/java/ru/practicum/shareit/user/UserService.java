package ru.practicum.shareit.user;

import java.util.Collection;

public interface UserService {
    void delUserById(long id);
    User findUserById(long id);
    User update(User newUser,long id);
    User create(User user);
    Collection<User> findAll();
}
