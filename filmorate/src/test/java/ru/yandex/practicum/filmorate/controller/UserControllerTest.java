package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {
    private final String uRL = "/user";

    @MockBean
    private UserController userController;

    @SpyBean
    private UserRepository userRepository;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void validationUserFieldsWhenRequestComesTest() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        User user = User.builder()
                .email("billy@yandex.ru")
                .login("DeepFantasy")
                .name("Billy Harrington")
                .birthday(LocalDate.of(1969, 7, 14))
                .build();

        Mockito.when(userController.addUser(user)).thenReturn(user);

        mvc.perform(post(uRL)
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(user)));

        //почта пустая
        user.setEmail("");
        requestSender(user);

        //почта без @
        user.setEmail("billyyandex.ru");
        requestSender(user);


        //логин не может быть пустым и содержать пробелы
        user.setLogin("   ");
        requestSender(user);
        user.setLogin("DeepFantasy");

        //name может быть пустым
        user.setName("");
        requestSender(user);

        //присвоение к имени логина, если имя пустое
        assertThat(user.getName().isEmpty()).isTrue();
        user = userRepository.addUser(user);
        assertThat(user.getName()).isEqualTo(user.getEmail());

        //др в будущем
        user.setBirthday(LocalDate.of(2300,12,12));
        requestSender(user);
    }

    public void requestSender(User user) throws Exception {
        mvc.perform(post(uRL)
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult ->
                        assertTrue(mvcResult.getResolvedException() instanceof MethodArgumentNotValidException));
    }
}