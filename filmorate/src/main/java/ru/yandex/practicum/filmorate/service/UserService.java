package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User addUser(User user) {
        //Optional<User> userOptional = Optional.ofNullable(userRepository.addUser(user));
        return userRepository.addUser(user);
    }

    public User updateUser(User user) {
        return userRepository.updateUser(user);
    }

    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }
}
