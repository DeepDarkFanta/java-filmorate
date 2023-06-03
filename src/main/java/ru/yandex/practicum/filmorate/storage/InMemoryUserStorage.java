package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.util.exception.ValidationException;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users;

    private final AtomicInteger id;

    @Autowired
    public InMemoryUserStorage() {
        this.users = new HashMap<>();
        this.id = new AtomicInteger();
    }

    @Override
    public User addUser(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        user.setId(id.incrementAndGet());
        users.put(id.get(), user);
        log.info("User has been added");
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (users.containsKey(user.getId())) {
            users.put((int) user.getId(), user);
            log.info("User has been updated");
        } else {
            throw new ValidationException("There is no such ID user");
        }
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public void checkUserById(int id) {
        if (!users.containsKey(id)) {
            throw new ValidationException("There is no such ID user");
        }
    }
}
