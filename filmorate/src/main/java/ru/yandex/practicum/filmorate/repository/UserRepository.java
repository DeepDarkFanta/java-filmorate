package ru.yandex.practicum.filmorate.repository;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.util.exception.ValidationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserRepository {
    private final HashMap<Integer, User> users;
    private int id;

    public User addUser(User user) {
        if (user.getName().isEmpty()) {
            user.setName(user.getEmail());
        }
        user.setId(id);
        users.put(id++, user);
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
