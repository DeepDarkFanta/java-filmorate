package ru.yandex.practicum.filmorate.repository;

import lombok.Data;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Data
@Component
public class UserRepository {
    private final HashMap<Integer, User> users;
    private int id;

    public User addUser(User user) {
        user.setId(id);
        users.put(id++, user);
        return user;
    }

    public User updateUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }
}
