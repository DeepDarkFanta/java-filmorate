package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.impl.UserDaoImpl;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDBStorage;

import java.util.*;

@Service
public class UserService {
    private final UserDBStorage userDBStorage;

    private final UserDaoImpl userDao;

    @Autowired
    public UserService(UserDBStorage userDBStorage, UserDaoImpl userDao) {
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
    }

    public void addFriend(Long id, Long friendId) {
        userDao.addFriend(id, friendId);
    }

    public void deleteFriend(Long id, Long friendId) {
        userDao.deleteFriend(id, friendId);
    }

    public List<User> getFriends(Long id) {
        return userDao.getFriends(id);
    }

    public List<User> getCommonFriends(Long id, Long otherId) {
        return userDao.getCommonFriends(id, otherId);
    }
}
