package ru.yandex.practicum.filmorate.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql({"/sql/schemaTest.sql"})
@Sql({"/schema.sql"})
public class UserDBStorageTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private final UserController userController;

    @Autowired
    private final JdbcTemplate jdbcTemplate;

    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private User user;

    @BeforeEach
    public void createUser() {
        user = new User();
        user.setLogin("friend");
        user.setName("friend adipisicing");
        user.setEmail("asd@gmail.com");
        user.setBirthday(LocalDate.of(2010,10,10));
    }

    @Test
    public void injectBeanTest() {
        assertThat(userController).isNotNull();
    }

    @Test
    public void addUserTest() throws Exception {
        String result =  objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(result))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.login").value(user.getLogin()))
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect((jsonPath("$.email")).value(user.getEmail()))
                .andExpect(jsonPath("$.birthday").value(user.getBirthday().toString()));

        assertThat(jdbcTemplate.query(
                "SELECT * FROM USERS WHERE ID = 1",
                new BeanPropertyRowMapper<>(User.class)).get(0)
        ).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(user);
    }

    @Test
    public void updateUserTest() throws Exception {
        user.setName("123");
        user.setId(1);
        user.setLogin("123");
        user.setEmail("123@asd.ru");
        user.setBirthday(LocalDate.of(2000,1,1));
        userController.addUser(user);
        mockMvc.perform(
                put("/users")
                .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.login").value(user.getLogin()))
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect((jsonPath("$.email")).value(user.getEmail()))
                .andExpect(jsonPath("$.birthday").value(user.getBirthday().toString()));

        assertThat(
                jdbcTemplate.query(
                "SELECT * FROM USERS WHERE ID = 1",
                new BeanPropertyRowMapper<>(User.class)).get(0)
        )
                .usingRecursiveComparison()
                .isEqualTo(user);
    }

    @Test
    public void getListUsersTest() {
        List<User> userList = new ArrayList<>();
        userList.add(user);
        userController.addUser(user);
        User user1 = jdbcTemplate.query(
                "SELECT * FROM USERS WHERE ID = 1",
                new BeanPropertyRowMapper<>(User.class)).get(0);
        assertThat(
                jdbcTemplate.query(
                        "SELECT * FROM USERS WHERE ID = 1",
                        new BeanPropertyRowMapper<>(User.class))
        )
                .usingRecursiveComparison()
                .isEqualTo(userList);
    }
}
