package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {
    private final String uRL = "/users";

    @MockBean
    private UserController userController;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;

    @BeforeEach
    public void createUser() {
        user = User.builder()
                .email("billy@yandex.ru")
                .login("DeepFantasy")
                .name("Billy Harrington")
                .birthday(LocalDate.of(1969, 7, 14))
                .build();
    }

    @Test
    public void validationUserWhenAllFieldsIsRightTest() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        Mockito.when(userController.addUser(user)).thenReturn(user);

        mvc.perform(post(uRL)
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(user)));
    }

    @Test
    public void userWithEmptyEmailTest() throws Exception {
        user.setEmail("");
        assertThat(requestSender(user)).isTrue();
    }

    @Test
    public void userEmailWithoutAtTest() throws Exception {
        user.setEmail("billyyandex.ru");
        assertThat(requestSender(user)).isTrue();
    }

    @Test
    public void userLoginWithSpacesTest() throws Exception {
        user.setLogin("   ");
        assertThat(requestSender(user)).isTrue();
    }

    @Test
    public void userNameCanEmptyTest() throws Exception {
        user.setName("");
        assertThat(requestSender(user)).isFalse();
    }

    @Test
    public void userBirthdayDateInTheFutureTest() throws Exception {
        user.setBirthday(LocalDate.of(2300,12,12));
        assertThat(requestSender(user)).isTrue();
    }

    public boolean requestSender(User user) throws Exception {
        AtomicBoolean isThrowException = new AtomicBoolean(false);
        mvc.perform(post(uRL)
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(mvcResult ->
                        isThrowException.set(mvcResult.getResolvedException() instanceof MethodArgumentNotValidException));
        return isThrowException.get();
    }
}