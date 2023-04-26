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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FilmController.class)
@AutoConfigureMockMvc
class FilmControllerTest {
    private final String URL = "/film";

    @MockBean
    private FilmController filmController;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void validationFieldsWhenRequestComesTest() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        Film film = Film.builder()
                .description("Gachimuchi is an Internet meme and subculture that originated in " +
                        "August 2007 on the Japanese video hosting site Nico Nico Douga.")
                .name("Billy Harrington")
                .releaseDate(LocalDate.of(2012,12,12))
                .duration(120)
                .build();
        String asd = objectMapper.writeValueAsString(film);
        System.out.println(asd);

        Mockito.when(filmController.addFilm(film)).thenReturn(film);

        //все поля правильные
        mvc.perform(post(URL)
                .content(objectMapper.writeValueAsString(film))
                .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(film)));

        //поле name пустое
        film.setName("");
        requestSender(film);

        film.setName("Billy Harrington");

        //поле description более 200 символов
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

        requestSender(film);

        film.setDescription("Gachimuchi is an Internet meme and subculture that originated in \" +\n" +
                "\"August 2007 on the Japanese video hosting site Nico Nico Douga.");

        //дата релиза — раньше 28.12.1895
        film.setReleaseDate(LocalDate.of(1895, 11, 28));
        requestSender(film);

        film.setReleaseDate(LocalDate.of(2010, 11, 28));

        //продолжительность фильма отрицательна.
        film.setDuration(-20);
        requestSender(film);
    }

    public void requestSender(Film film) throws Exception {
        mvc.perform(post(URL)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult ->
                        assertTrue(mvcResult.getResolvedException() instanceof MethodArgumentNotValidException));
    }
}
