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
        String sql = "SELECT * FROM USER WHERE id = ?";
        try {
            return jdbcTemplate.query(sql, USER, id).get(0);
        } catch (EmptyResultDataAccessException  | NullPointerException e) {
            throw new ValidationException("There is no such ID =" + id + " user");
        }
    }
/*    "MERGE INTO FRIENDS_STATUS\n" +
            "    USING (SELECT ZERO() + (SELECT FRIEND_ONE_ID\n" +
            "                            FROM FRIENDS_STATUS\n" +
            "                            WHERE (FRIEND_ONE_ID = ?\n" +
            "                              AND FRIEND_TWO_ID = ?)\n" +
            "                               OR (FRIEND_ONE_ID = ?\n" +
            "                              AND FRIEND_TWO_ID = ?)) As b) As r\n" +
            "    ON (r.b IS NOT NULL AND r.b <> ?)\n" +
            "    WHEN MATCHED\n" +
            "        THEN UPDATE\n" +
            "             SET STATUS = 2\n" +
            "WHEN NOT MATCHED THEN INSERT(FRIEND_ONE_ID, FRIEND_TWO_ID, STATUS)\n" +
            "                      VALUES (?,?,1);"*/

    @Override
    public void addFriend(Long id, Long idFriend) {// 1 -> 2, 2 -> 1
        try {
            jdbcTemplate.update(
                    "MERGE INTO FRIENDS_STATUS USING (SELECT ZERO() + (SELECT FRIEND_ONE_ID FROM FRIENDS_STATUS" +
                            " WHERE (FRIEND_ONE_ID = ? AND FRIEND_TWO_ID = ?) OR (FRIEND_ONE_ID = ? AND FRIEND_TWO_ID = ?)) As b) As r" +
                            " ON (r.b IS NOT NULL AND r.b <> ?) WHEN MATCHED THEN UPDATE SET STATUS = 2 WHEN NOT MATCHED THEN INSERT(" +
                            "FRIEND_ONE_ID, FRIEND_TWO_ID, STATUS) VALUES (?,?,1)"
                    , id, idFriend, idFriend, id, id, id, idFriend
            );
        } catch (Throwable e) {
            throw new ValidationException("There is no such id");
        }
        /*String sql = "SELECT status FROM FRIENDS_STATUS WHERE friend_one_id = ?" +
                " AND friend_two_id = ?";
        SqlRowSet res =  jdbcTemplate.queryForRowSet(sql, idFriend, id);
        SqlRowSet resWasFriend =  jdbcTemplate.queryForRowSet(sql, id, idFriend);
        if (res.next()) {
            jdbcTemplate.update("UPDATE FRIENDS_STATUS SET status = 2 " +
                    "WHERE friend_one_id = ? AND friend_two_id = ?", idFriend, id);
        } else if (resWasFriend.next()) {
            jdbcTemplate.update("UPDATE FRIENDS_STATUS SET status = 2 " +
                    "WHERE friend_one_id = ? AND friend_two_id = ?", id, idFriend);
        } else if (!res.previous() && !resWasFriend.previous())  {
            jdbcTemplate.update("INSERT INTO FRIENDS_STATUS SET friend_one_id = ?, friend_two_id = ?, " +
                            "status = 1", id, idFriend);
        } else {
            throw new ValidationException("Something was wrong");
        }*/
    }

    @Override
    public void deleteFriend(Long id, Long idFriend) {
        jdbcTemplate.update(
                "DELETE FROM FRIENDS_STATUS WHERE (FRIEND_ONE_ID = ? " +
                "AND FRIEND_TWO_ID = ?) OR (FRIEND_ONE_ID = ? AND FRIEND_TWO_ID = ?)",
                id, idFriend, idFriend, id
        );
        /*String sql = "SELECT status FROM FRIENDS_STATUS WHERE friend_one_id = ?" +
                " AND friend_two_id = ? OR  friend_one_id = ? AND friend_two_id = ?";

        SqlRowSet resWasFriend =  jdbcTemplate.queryForRowSet(sql, id, idFriend);
        SqlRowSet res = jdbcTemplate.queryForRowSet(sql, idFriend, id);

        if (res.next() *//*&& (res.getInt("status") == 1 || res.getInt("status") == 2)*//*) {
            jdbcTemplate.update("DELETE FROM FRIENDS_STATUS WHERE FRIEND_ONE_ID = ? AND FRIEND_TWO_ID = ?",
                    idFriend, id);
        } else if (resWasFriend.next() *//*&& (resWasFriend.getInt("status") == 1
                || resWasFriend.getInt("status") == 2)*//*) {
            jdbcTemplate.update("DELETE FROM FRIENDS_STATUS WHERE FRIEND_ONE_ID = ? AND FRIEND_TWO_ID = ?",
                    id, idFriend);
        } else {
            throw new ValidationException("Something was wrong");
        }*/
    }

    @Override
    public List<User> getFriends(Long id) {
        return jdbcTemplate.query(
                "SELECT * FROM USER WHERE ID IN (SELECT" +
                " CASE WHEN FRIEND_ONE_ID = ? THEN FRIEND_TWO_ID" +
                " WHEN FRIEND_TWO_ID = ? AND STATUS = 2 THEN FRIEND_ONE_ID END FROM FRIENDS_STATUS)",
                USER,
                id, id
        );
    }

    @Override
    public List<User> getCommonFriends(Long id, Long otherId) {
        return jdbcTemplate.query(
                "SELECT * FROM `USER` WHERE ID IN (SELECT tableFriend1 FROM (SELECT CASE WHEN FRIEND_ONE_ID = ? THEN FRIEND_TWO_ID" +
                        "  WHEN FRIEND_TWO_ID = ? AND STATUS = 2 THEN FRIEND_ONE_ID END As tableFriend1 FROM FRIENDS_STATUS) As table1 LEFT JOIN (SELECT" +
                        " CASE WHEN FRIEND_ONE_ID = ? THEN FRIEND_TWO_ID WHEN FRIEND_TWO_ID = ? AND STATUS = 2 THEN FRIEND_ONE_ID END As tableFriends2 FROM" +
                        " FRIENDS_STATUS) As table2 ON (table1.tableFriend1 = table2.tableFriends2) WHERE table1.tableFriend1 " +
                        "IS NOT NULL AND table2.tableFriends2 IS NOT NULL);",
                USER,
                id, id, otherId, otherId
        );
    }
}
