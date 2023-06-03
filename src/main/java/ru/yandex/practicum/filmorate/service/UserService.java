package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.impl.UserDaoImpl;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserDBStorage;
import ru.yandex.practicum.filmorate.util.exception.ValidationException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final InMemoryUserStorage inMemoryUserStorage;
    private final UserDBStorage userDBStorage;
    private final UserDaoImpl userDao;

    @Autowired
    public UserService(InMemoryUserStorage inMemoryUserStorage, UserDBStorage userDBStorage, UserDaoImpl userDao) {
        this.inMemoryUserStorage = inMemoryUserStorage;
        this.userDBStorage = userDBStorage;
        this.userDao = userDao;
    }

    public User addUser(User user) {
        return userDBStorage.addUser(user);
    }

    public User updateUser(User user) {
        return userDBStorage.updateUser(user);
    }

    public List<User> getAllUsers() {
        return userDBStorage.getAllUsers();
    }

    public User getUserById(Long id) {
        return userDao.getUserById(id);
        /*return inMemoryUserStorage.getAllUsers().stream()
                .filter(x -> x.getId() == id)
                .findFirst()
                .orElseThrow(() -> new ValidationException("There is no such ID user"));*/
    }

    public void addFriend(Long id, Long friendId) {
        userDao.addFriend(id, friendId);
        /*if (id == friendId) throw new ValidationException("you can't be friends with yourself");
        User user = getUserById(id);
        User friend = getUserById(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(id);*/
    }

    public void deleteFriend(Long id, Long friendId) {
        userDao.deleteFriend(id, friendId);
        /*
        User user = getUserById(id);
        User friend = getUserById(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(id);*/
    }

    public List<User> getFriends(Long id) {
        return userDao.getFriends(id);
       /* User user = getUserById(id);
        return inMemoryUserStorage.getAllUsers().stream()
                .filter(x -> user.getFriends().contains(x.getId()))
                .collect(Collectors.toList());*/
    }

    public List<User> getCommonFriends(Long id, Long otherId) {
        return userDao.getCommonFriends(id, otherId);
       /* User user = getUserById(id);
        User friend = getUserById(otherId);
        Set<Integer> commonFriends = new HashSet<>(user.getFriends());
        commonFriends.retainAll(friend.getFriends());
        return inMemoryUserStorage.getAllUsers().stream()
                .filter(x -> commonFriends.contains(x.getId()))
                .collect(Collectors.toList());*/
    }
}
