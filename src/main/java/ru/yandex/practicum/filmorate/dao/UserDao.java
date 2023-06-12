package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserDao {
    User getUserById(long id);

    void addFriend(Long id, Long idFriend);

    void deleteFriend(Long id, Long idFriend);

    List<User> getFriends(Long id);

    List<User> getCommonFriends(Long id, Long otherId);
}
