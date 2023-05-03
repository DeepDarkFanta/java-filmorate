package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.InMemoryUserStorage;

import java.util.List;

@Service
public class UserService {
    private final InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    public UserService(InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public User addUser(User user) {
        return inMemoryUserStorage.addUser(user);
    }

    public User updateUser(User user) {
        return inMemoryUserStorage.updateUser(user);
    }

    public List<User> getAllUsers() {
        return inMemoryUserStorage.getAllUsers();
    }
}
