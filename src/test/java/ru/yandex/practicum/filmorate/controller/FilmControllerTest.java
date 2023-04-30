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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FilmController.class)
@AutoConfigureMockMvc
class FilmControllerTest {
    private final String uRL = "/films";

    @MockBean
    private FilmController filmController;

    @Autowired
    private MockMvc mvc;

    /*
    "Создай отдельный метод для инициализации ObjectMapper, чтобы избежать повторения кода.
     Можно сделать это с использованием аннотации @beforeeach"
     так я один раз его инициализирую, зачем перед каждым тестом
     ------
     Также по поводу @SpyBean - использовал для теста репы на присвоение к name -> login-а
     когда name пустой. СпайБин позволяет же использовать методы класса. Если бы делал через mock,
     то он бы нулл возвращал у метода, где это присвоение происходит. Убрал пока эту проверку, так как по ТЗ
     она не требуется, но все таки как лучше надо было сделать?
     */
    @Autowired
    private ObjectMapper objectMapper;

    private Film film;
    @BeforeEach
    public void createFilm() {
        film = Film.builder()
                .description("Gachimuchi is an Internet meme and subculture that originated in " +
                        "August 2007 on the Japanese video hosting site Nico Nico Douga.")
                .name("Billy Harrington")
                .releaseDate(LocalDate.of(2012,12,12))
                .duration(120)
                .build();
    }

    @Test
    public void filmFieldsAllIsRightTest() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        Mockito.when(filmController.addFilm(film)).thenReturn(film);

        mvc.perform(post(uRL)
                .content(objectMapper.writeValueAsString(film))
                .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(film)));
    }

    @Test
    public void filmNameIsEmptyTest() throws Exception {
        film.setName("");
        assertThat(requestSender(film)).isTrue();
    }

    @Test
    public void filmDescriptionMoreThen200SymbolsTest() throws Exception {
        film.setDescription("Валидация\n" +
                "Проверьте данные, которые приходят в запросе на добавление нового фильма или пользователя. Эти данные должны соответствовать определённым критериям. \n" +
                "Для Film:\n" +
                "название не может быть пустым;\n" +
                "максимальная длина описания — 200 символов;\n" +
                "дата релиза — не раньше 28 декабря 1895 года;\n" +
                "продолжительность фильма должна быть положительной.\n" +
                "Для User:\n" +
                "электронная почта не может быть пустой и должна содержать символ @;\n" +
                "логин не может быть пустым и содержать пробелы;\n" +
                "имя для отображения может быть пустым — в таком случае будет использован логин;\n" +
                "дата рождения не может быть в будущем.");

        assertThat(requestSender(film)).isTrue();
    }

    @Test
    public void filmDateReleaseLaterThen1895y11m28dTest() throws Exception {
        film.setReleaseDate(LocalDate.of(1895, 11, 28));
        assertThat(requestSender(film)).isTrue();
    }

    @Test
    public void filmDurationNegativeTest() throws Exception {
        film.setDuration(-20);
       assertThat(requestSender(film)).isTrue();
    }

    public boolean requestSender(Film film) throws Exception {
        AtomicBoolean isThrowException = new AtomicBoolean(false);
        mvc.perform(post(uRL)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult ->
                        isThrowException.set(mvcResult.getResolvedException() instanceof MethodArgumentNotValidException));
        return isThrowException.get();
    }
}
