package ru.yandex.practicum.filmorate.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.util.exception.ValidationException;

import java.util.List;

@Repository
public class UserDaoImpl implements UserDao {
    private final JdbcTemplate jdbcTemplate;

    private static final BeanPropertyRowMapper<User> USER =
            BeanPropertyRowMapper.newInstance(User.class);

    @Autowired
    public UserDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User getUserById(long id) {
        String sql = "SELECT * FROM USERS WHERE id = ?";
        try {
            List<User> users = jdbcTemplate.query(sql, USER, id);
            if (users.isEmpty()) throw new ValidationException("There is no such ID =" + id + " user");
            return users.get(0);
        } catch (EmptyResultDataAccessException e) {
            throw new ValidationException("There is no such ID =" + id + " user");
        }
    }

    @Override
    public void addFriend(Long id, Long idFriend) {
        try {
            jdbcTemplate.update(
                    "MERGE INTO FRIENDS_STATUS USING (SELECT ZERO() + (SELECT FRIEND_ONE_ID FROM FRIENDS_STATUS" +
                            " WHERE (FRIEND_ONE_ID = ? AND FRIEND_TWO_ID = ?) OR (FRIEND_ONE_ID = ? AND FRIEND_TWO_ID = ?)) As b) As r" +
                            " ON (r.b IS NOT NULL AND r.b <> ?) WHEN MATCHED THEN UPDATE SET STATUS = 2 WHEN NOT MATCHED THEN INSERT(" +
                            "FRIEND_ONE_ID, FRIEND_TWO_ID, STATUS) VALUES (?,?,1)",
                    id, idFriend, idFriend, id, id, id, idFriend
            );
        } catch (Throwable e) {
            throw new ValidationException("There is no such id");
        }
    }

    @Override
    public void deleteFriend(Long id, Long idFriend) {
        jdbcTemplate.update(
                "DELETE FROM FRIENDS_STATUS WHERE (FRIEND_ONE_ID = ? " +
                "AND FRIEND_TWO_ID = ?) OR (FRIEND_ONE_ID = ? AND FRIEND_TWO_ID = ?)",
                id, idFriend, idFriend, id
        );
    }

    @Override
    public List<User> getFriends(Long id) {
        return jdbcTemplate.query(
                "SELECT * FROM USERS WHERE ID IN (SELECT" +
                " CASE WHEN FRIEND_ONE_ID = ? THEN FRIEND_TWO_ID" +
                " WHEN FRIEND_TWO_ID = ? AND STATUS = 2 THEN FRIEND_ONE_ID END FROM FRIENDS_STATUS)",
                USER,
                id, id
        );
    }

    @Override
    public List<User> getCommonFriends(Long id, Long otherId) {
        return jdbcTemplate.query(
                "SELECT * FROM USERS WHERE ID IN (SELECT tableFriend1 FROM (SELECT CASE WHEN FRIEND_ONE_ID = ? THEN FRIEND_TWO_ID" +
                        "  WHEN FRIEND_TWO_ID = ? AND STATUS = 2 THEN FRIEND_ONE_ID END As tableFriend1 FROM FRIENDS_STATUS) As table1 LEFT JOIN (SELECT" +
                        " CASE WHEN FRIEND_ONE_ID = ? THEN FRIEND_TWO_ID WHEN FRIEND_TWO_ID = ? AND STATUS = 2 THEN FRIEND_ONE_ID END As tableFriends2 FROM" +
                        " FRIENDS_STATUS) As table2 ON (table1.tableFriend1 = table2.tableFriends2) WHERE table1.tableFriend1 " +
                        "IS NOT NULL AND table2.tableFriends2 IS NOT NULL);",
                USER,
                id, id, otherId, otherId
        );
    }
}
