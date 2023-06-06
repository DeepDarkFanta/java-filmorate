package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.FilmDBStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql({"/sql/schema.sql"})
public class FilmDaoTest {
    @Autowired
    private final FilmDao filmDao;
    @Autowired
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    private final FilmDBStorage filmDBStorage;
    @Autowired
    private final FilmController filmController;
    @Autowired
    private final UserController userController;
    private Film film;
    private User user;

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

        user = new User();
        user.setLogin("friend");
        user.setName("friend adipisicing");
        user.setEmail("asd@gmail.com");
        user.setBirthday(LocalDate.of(2010,10,10));
    }

    @Test
    public void getFilmByIdTest() {
        Film filmFromController = filmController.addFilm(film);
        assertThat(filmFromController)
                .usingRecursiveComparison()
                .isEqualTo(film);

        Film filmFromDB = filmController.getFilmById(1L);

        assertThat(filmFromDB)
                .usingRecursiveComparison()
                .isEqualTo(film);
    }

    @Test
    public void getGenresByFilmIdTest() {
        List<Genres> genresList = filmController.addFilm(film).getGenres();
        assertThat(film.getGenres())
                .usingRecursiveComparison()
                .isEqualTo(genresList);
    }

    @Test
    public void likeFilmTest() {
        userController.addUser(user);
        userController.addUser(user);
        filmController.addFilm(film);
        filmController.likeFilm(1L, 1L);
        Long userIdLikeFromDb = jdbcTemplate.queryForObject(
                "SELECT USERID FROM LIKES WHERE USERID = 1 AND FILMID = 1;",
                    Long.class
                );
        assertThat(userIdLikeFromDb).isEqualTo(1L);
    }

    @Test
    public void deleteLike() {
        userController.addUser(user);
        filmController.addFilm(film);
        filmController.likeFilm(1L, 1L);
        filmController.deleteLike(1L, 1L);
        List<Long> userIdLikeFromDb = jdbcTemplate.queryForList(
                "SELECT USERID FROM LIKES WHERE USERID = 1 AND FILMID = 1;",
                Long.class
        );
        assertThat(userIdLikeFromDb.isEmpty()).isTrue();
    }

    @Test
    public void getTopFilms() {
        userController.addUser(user);
        userController.addUser(user);
        filmController.addFilm(film);
        filmController.addFilm(film);
        filmController.likeFilm(2L, 1L);
        filmController.likeFilm(2L, 2L);
        List<Film> films = filmController.getTopFilms(3);
        assertThat(films.size()).isEqualTo(2);
        assertThat(films.get(0).getId()).isEqualTo(2L);
        assertThat(films.get(1).getId()).isEqualTo(1L);
    }
}
