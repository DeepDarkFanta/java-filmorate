package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.util.exception.ValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserRepository {
    private final Map<Integer, User> users;
    private final AtomicInteger id = new AtomicInteger();

    public User addUser(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        user.setId(id.incrementAndGet());
        users.put(id.get(), user);
        log.info("User has been added");
        return user;
    }

    public User updateUser(User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.info("User has been updated");
        } else {
            throw new ValidationException("There is no such ID user");
        }
        return user;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }
}
