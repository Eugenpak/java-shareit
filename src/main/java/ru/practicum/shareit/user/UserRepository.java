package ru.practicum.shareit.user;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository {
    boolean delUserById(long id);

    Optional<User> findUserById(long id);

    User update(User user);

    User create(User user);

    Collection<User> findAll();
}
