package ru.practicum.shareit.user;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
@Slf4j
@NoArgsConstructor
public class UserRepositoryInMemory implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @Override
    public Collection<User> findAll() {
        log.debug("UserRepositoryInMemory findAll().");
        return users.values();
    }

    @Override
    public User create(User user) {
        log.debug("UserRepositoryInMemory create(user:{}).", user);
        user.setId(getNextId());
        users.put(user.getId(),user);
        return user;
    }

    @Override
    public User update(User user) {
        log.debug("UserRepositoryInMemory update(user:{}).", user);
        users.put(user.getId(),user);
        return user;
    }

    @Override
    public Optional<User> findUserById(long id) {
        log.debug("UserRepositoryInMemory findUserById(id:{}).", id);
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public boolean delUserById(long id) {
        log.debug("UserRepositoryInMemory delUserById(id:{}).", id);
        User user = users.remove(id);
        return user != null;
    }

}
