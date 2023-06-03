package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genres;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.util.exception.ValidationException;

import java.util.List;

@Repository
public class FilmDBStorage implements FilmStorage {
    private final BeanPropertyRowMapper<Film> FILM =
            BeanPropertyRowMapper.newInstance(Film.class);

    private final FilmDao filmDao;

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDBStorage(FilmDao filmDao, JdbcTemplate jdbcTemplate) {
        this.filmDao = filmDao;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film addFilm(Film film) {
        film.setMpaId(
                film.getMpa().getId()
        );
        SqlParameterSource parameterSource = new BeanPropertySqlParameterSource(film);
        film.setId(
                new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("FILM")
                .usingGeneratedKeyColumns("id")
                .executeAndReturnKey(parameterSource)
                .longValue()
        );
        updateFilmGenre(film);
        jdbcTemplate.update(
                "INSERT INTO LIKES (FILMID) VALUES ( ? );",
                film.getId()
        );
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        //TODO: update genres
        int check = jdbcTemplate.update(
                "UPDATE FILM SET NAME = ?, DESCRIPTION = ?, RELEASEDATE = ?," +
                        " DURATION = ?, MPAID = ? WHERE ID = ?",
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );
        updateFilmGenre(film);
        if (check == 0) throw new ValidationException("there is no such id = " + film.getId());
        return film;
    }

    @Override
    public List<Film> getListFilms() {
        List<Film> films = jdbcTemplate.query(
                "SELECT * FROM FILM;",
                FILM
        );
        for (Film film : films) {
            List<Genres> genres = filmDao.getGenresByFilmId(film.getId());
            Mpa mpa = filmDao.getMpaByFilmId(film.getId());
            film.setGenres(genres);
            film.setMpa(mpa);
        }
        return films;
    }

    private void updateFilmGenre(Film film) {
        jdbcTemplate.update(
                "DELETE FROM FILM_GENRE WHERE FILMID  = ?",
                film.getId()
        );
        if (film.getGenres().isEmpty()) return;
        StringBuilder sqlBuilderFilmGenres = new StringBuilder("INSERT INTO FILM_GENRE (FILMID, GENREID) VALUES ");
        for (Genres genre : film.getGenres()) {
            sqlBuilderFilmGenres.append("(")
                    .append(film.getId())
                    .append(", ")
                    .append(genre.getId())
                    .append("),");
        }
        sqlBuilderFilmGenres
                .deleteCharAt(sqlBuilderFilmGenres.length() - 1)
                .append(";");
        System.out.println(sqlBuilderFilmGenres);
        jdbcTemplate.update(
                sqlBuilderFilmGenres.toString()
        );
    }
}
