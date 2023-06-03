package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.util.exception.UserAndFilmErrorResponse;
import ru.yandex.practicum.filmorate.util.exception.ValidationException;

import java.time.LocalDate;
import java.util.List;

@Repository
public class UserDBStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private static final BeanPropertyRowMapper<User> USER =
            BeanPropertyRowMapper.newInstance(User.class);

    @Autowired
    public UserDBStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User addUser(User user) {
        if (user.getName().isEmpty()) user.setName(user.getLogin());
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("user")
                .usingGeneratedKeyColumns("id");
        SqlParameterSource parameterSource = new BeanPropertySqlParameterSource(user);
        user.setId(jdbcInsert.executeAndReturnKey(parameterSource).longValue());
        return user;
    }

    @Override
    public User updateUser(User user) {
        String sql = "UPDATE USER SET" +
                " email = ?, login = ?, name = ?, BIRTHDAY = ?" +
                " WHERE id = ?";

        int check = jdbcTemplate.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        if (check == 0) throw new ValidationException("there is no such id=" +
                user.getId());
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return jdbcTemplate.query("SELECT * FROM USER", USER);

    }
}
