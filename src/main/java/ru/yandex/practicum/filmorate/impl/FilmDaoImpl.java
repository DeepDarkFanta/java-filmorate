package ru.yandex.practicum.filmorate.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.util.exception.ValidationException;

import java.util.List;

@Repository
public class FilmDaoImpl implements FilmDao {
    private final JdbcTemplate jdbcTemplate;

    private final static BeanPropertyRowMapper<Film> FILM =
            BeanPropertyRowMapper.newInstance(Film.class);

    @Autowired
    public FilmDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film getFilmById(Long filmId) {
            Film film = jdbcTemplate.query(
                    "SELECT * FROM FILM WHERE ID = ?;",
                    FILM,
                    filmId
            ).get(0);
        film.setGenres(
                getGenresByFilmId(filmId)
        );
        film.setMpa(
                getMpaById(film.getMpaId())
        );
        return film;
    }

    @Override
    public List<Genres> getGenresByFilmId(Long filmId) {
        BeanPropertyRowMapper<Genres> genresBeanPropertyRowMapper =
                new BeanPropertyRowMapper<>(Genres.class);

        return jdbcTemplate.query(
                "SELECT GENREID As id, d.name As name FROM FILM_GENRE LEFT JOIN GENRE As d WHERE genreId = d.ID AND filmId = ?;",
                genresBeanPropertyRowMapper,
                filmId
        );
    }

    @Override
    public void likeFilm(Long id, Long userId) {
        jdbcTemplate.update(
                "INSERT INTO LIKES (FILMID, USERID) VALUES ( ?, ? )",
                id, userId
        );
    }

    @Override
    public void deleteLike(Long id, Long userId) {
        int response = jdbcTemplate.update(
                "DELETE FROM LIKES WHERE FILMID = ? AND USERID = ?",
                id, userId
        );
        if (response == 0) throw new ValidationException("there is no such id");
    }

    @Override
    public List<Film> getTopFilms(int count) {
        List<Film> films = jdbcTemplate.query(
                /*"SELECT * FROM FILM WHERE ID IN (SELECT FILMID FROM LIKES" +
                        " GROUP BY FILMID ORDER BY COUNT(USERID) desc LIMIT ?);"*/
                "SELECT film.*\n" +
                        "FROM film\n" +
                        "         INNER JOIN likes ON film.id = likes.filmId\n" +
                        "GROUP BY film.id\n" +
                        "ORDER BY COUNT(likes.filmId) DESC\n" +
                        "LIMIT ?;",
                FILM,
                count
        );
        for (Film film : films) {
            List<Genres> genres = getGenresByFilmId(film.getId());
            film.setGenres(genres);
            film.setMpa(
                    getMpaById(film.getMpaId())
            );
        }
        return films;
    }

    @Override
    public List<Genre> getGenres() {
        return jdbcTemplate.query(
                "SELECT * FROM GENRE;",
                new BeanPropertyRowMapper<>(Genre.class)
        );
    }

    @Override
    public Genre getGenreById(Long id) {
        return jdbcTemplate.query("SELECT * FROM GENRE WHERE ID = ?;",
                new BeanPropertyRowMapper<>(Genre.class),
                id).get(0);
    }

    @Override
    public List<MpaWithName> getAllMpa() {
        return jdbcTemplate.query(
                "SELECT * FROM MPA_TYPE;",
                new BeanPropertyRowMapper<>(MpaWithName.class)
        );
    }

    @Override
    public MpaWithName getMpaById(Long id) {
        BeanPropertyRowMapper<MpaWithName> mpa = new BeanPropertyRowMapper<>(MpaWithName.class);
        return jdbcTemplate.query(
                "SELECT * FROM MPA_TYPE WHERE ID = ?;",
                mpa,
                id
        ).get(0);
    }

    @Override
    public MpaWithName getMpaByFilmId(Long filmId) {
        return jdbcTemplate.query(
                "SELECT MPAID As id, MPA_TYPE.NAME As name FROM FILM, MPA_TYPE WHERE FILM.ID = ?;",
                new BeanPropertyRowMapper<>(MpaWithName.class),
                filmId
        ).get(0);
    }
}
