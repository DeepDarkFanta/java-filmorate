package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.util.exception.ValidationException;

import java.util.*;
import java.util.stream.Collectors;

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

    public User getUserById(int id) {
        return inMemoryUserStorage.getAllUsers().stream()
                .filter(x -> x.getId() == id)
                .findFirst()
                .orElseThrow(() -> new ValidationException("There is no such ID user"));
    }

    public void addFriend(int id, int friendId) {
        if (id == friendId) throw new ValidationException("you can't be friends with yourself");
        User user = getUserById(id);
        User friend = getUserById(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(id);
    }

    public void deleteFriend(int id, int friendId) {
        User user = getUserById(id);
        User friend = getUserById(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(id);
    }

    public List<User> getFriends(int id) {
        User user = getUserById(id);
        return inMemoryUserStorage.getAllUsers().stream()
                .filter(x -> user.getFriends().contains(x.getId()))
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(int id, int otherId) {
        User user = getUserById(id);
        User friend = getUserById(otherId);
        Set<Integer> commonFriends = new HashSet<>(user.getFriends());
        commonFriends.retainAll(friend.getFriends());
        return inMemoryUserStorage.getAllUsers().stream()
                .filter(x -> commonFriends.contains(x.getId()))
                .collect(Collectors.toList());
    }
}
