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
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genres;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.MpaWithName;

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
@Sql({"/sql/schema.sql"})
public class FilmDBStorageTest {
    @Autowired
    private final FilmDBStorage filmDBStorage;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    private final FilmController filmController;
    private Film film;
    ObjectMapper objectMapper =
            new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    public void createFilm() {
        film = new Film();
        film.setName("new film");
        film.setReleaseDate(LocalDate.of(2020,12,12));
        film.setDuration(120);
        MpaWithName mpa = new MpaWithName();
        mpa.setId(1L);
        film.setMpa(mpa);
        ArrayList<Genres> genres = new ArrayList<>();
        Genres genres1 = new Genres();
        genres1.setId(1);
        genres.add(genres1);
        film.setGenres(genres);
    }

    @Test
    public void addFilmTest() throws Exception{
        String result = objectMapper.writeValueAsString(film);
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(result))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
        Film filmFromDB =  filmDBStorage.getListFilms().get(0);

        assertThat(filmFromDB.getId()).isEqualTo(1L);

        assertThat(filmFromDB.getGenres().get(0).getName()).isEqualTo("Комедия");

        assertThat(filmFromDB.getMpa().getName()).isEqualTo("G");

        assertThat(filmFromDB)
                .usingRecursiveComparison()
                .ignoringFields("genres", "mpa", "id", "mpaId")
                .isEqualTo(film);
    }

    @Test
    public void updateFilmTest() throws Exception {
        filmController.addFilm(film);

        film.setName("new film 1");
        film.setReleaseDate(LocalDate.of(2010,12,12));
        film.setDuration(121);
        MpaWithName mpa = new MpaWithName();
        mpa.setId(2L);
        film.setMpa(mpa);
        ArrayList<Genres> genres = new ArrayList<>();
        Genres genres1 = new Genres();
        genres1.setId(4);
        genres.add(genres1);
        film.setGenres(genres);
        film.setId(1L);

        filmController.updateFilm(film);
        mockMvc.perform(put("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(film.getId()));
        Film filmFromDb = filmController.getAllFilms().get(0);

        assertThat(filmFromDb.getMpa().getId()).isEqualTo(2L);
        assertThat(filmFromDb.getMpa().getName()).isEqualTo("PG");
        assertThat(filmFromDb.getGenres().get(0).getId()).isEqualTo(4L);
        assertThat(filmFromDb.getGenres().get(0).getName()).isEqualTo("Триллер");
    }

    @Test
    public void getListFilms() {
        filmController.addFilm(film);
        filmController.addFilm(film);
        List<Film> filmsFromDb = filmController.getAllFilms();
        List<Film> films = new ArrayList<>();
        films.add(film);
        films.add(film);
        assertThat(filmsFromDb)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .ignoringFields("genres")
                .isEqualTo(films);
        assertThat(
                jdbcTemplate.query(
                        "SELECT * FROM FILM;",
                        new BeanPropertyRowMapper<>(Film.class)
                )
        )
                .usingRecursiveComparison()
                .ignoringFields("id")
                .ignoringFields("genres")
                .ignoringFields("mpa")
                .isEqualTo(films);
    }
}

