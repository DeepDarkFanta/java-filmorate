package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql({"/sql/schemaTest.sql"})
@Sql({"/sql/schema.sql"})
public class UserDaoTest {
    @Autowired
    private final UserController userController;

    @Autowired
    private final JdbcTemplate jdbcTemplate;
    private User user;

    private User friend;

    private User commonFriend;

    @BeforeEach
    public void createUser() {
        user = new User();
        user.setLogin("friend");
        user.setName("friend adipisicing");
        user.setEmail("asd@gmail.com");
        user.setBirthday(LocalDate.of(2010,10,10));

        friend = new User();
        friend.setLogin("friend");
        friend.setName("friend adipisicing");
        friend.setEmail("asd@gmail.com");
        friend.setBirthday(LocalDate.of(2010,10,10));

        commonFriend = new User();
        commonFriend.setLogin("friend");
        commonFriend.setName("friend adipisicing");
        commonFriend.setEmail("asd@gmail.com");
        commonFriend.setBirthday(LocalDate.of(2010,10,10));
    }

    @Test
     public void getUserByIdTest() {
        assertThat(
                userController.addUser(user)
        )
                .usingRecursiveComparison()
                .isEqualTo(user);

        assertThat(
                jdbcTemplate.query(
                        "SELECT * FROM USERS WHERE ID = 1",
                        new BeanPropertyRowMapper<>(User.class)).get(0)
        )
                .usingRecursiveComparison()
                .isEqualTo(user);
    }

    @Test
    public void addFriendAndGetFriendsTest() {
        userController.addUser(user);
        userController.addUser(friend);
        userController.addFriend(1L, 2L);

        assertThat(userController.getFriends(1L).get(0))
                .usingRecursiveComparison()
                .isEqualTo(friend);

        assertThat(userController.getFriends(2L).isEmpty()).isTrue();
        userController.addFriend(2L, 1L);

        assertThat(userController.getFriends(2L).get(0))
                .usingRecursiveComparison()
                .isEqualTo(user);
    }

    @Test
    public void deleteFriendTest() {
        userController.addUser(user);
        userController.addUser(friend);
        userController.addFriend(1L, 2L);
        userController.addFriend(2L, 1L);
        userController.deleteFriend(2L, 1L);
        assertThat(userController.getFriends(2L).isEmpty()).isTrue();
        assertThat(userController.getFriends(1L).isEmpty()).isTrue();
    }

    @Test
    public void getCommonFriendsTest() {
        userController.addUser(user);
        userController.addUser(friend);
        userController.addUser(commonFriend);
        userController.addFriend(1L, 2L);
        userController.addFriend(2L, 1L);

        userController.addFriend(3L, 1L);
        userController.addFriend(1L, 3L);
        assertThat(userController.getCommonFriends(3L, 2L).get(0))
                .usingRecursiveComparison()
                .isEqualTo(user);
    }
}
