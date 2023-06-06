package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genres;
import ru.yandex.practicum.filmorate.model.MpaWithName;
import ru.yandex.practicum.filmorate.util.exception.ValidationException;

import java.sql.PreparedStatement;
import java.sql.SQLException;
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
        film.setMpa(
                filmDao.getMpaByFilmId(film.getId())
        );
        updateFilmGenre(film);
        film.setGenres(
                filmDao.getGenresByFilmId(film.getId())
        );
        jdbcTemplate.update(
                "INSERT INTO LIKES (FILMID) VALUES ( ? );",
                film.getId()
        );
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        //TODO: update genres
        updateFilmGenre(film);
        film.setGenres(
                filmDao.getGenresByFilmId(film.getId())
        );
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
            MpaWithName mpa = filmDao.getMpaById(
                    film.getMpaId()
            );
            film.setGenres(genres);
            film.setMpa(mpa);
        }
        return films;
    }

    private void updateFilmGenre(Film film) {
        if (film.getGenres().size() == 0) {
            jdbcTemplate.update(
                    "DELETE FROM FILM_GENRE WHERE FILMID = ?",
                    film.getId()
            );
            return;
        }
        jdbcTemplate.update(
                "DELETE FROM FILM_GENRE WHERE FILMID = ?",
                film.getId()
        );
        //Странно конечно что когда некорректный запрос на апдейт приходит с повторяющимися индексами, то мы его обрабатываем
        //логичнее было бы сделать как я. Удалить все жанры которые не входят в новый объект с контроллера, а потом сделать вставку
        /*jdbcTemplate.batchUpdate(
                "DELETE FROM FILM_GENRE WHERE FILMID NOT IN " +
                        "(SELECT FILMID FROM FILM_GENRE As t WHERE t.FILMID = ? AND t.GENREID = ?) AND GENREID = ?;",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setLong(1, film.getGenres().get(i).getId());
                        ps.setLong(2, film.getId());
                        ps.setLong(3, film.getGenres().get(i).getId());
                    }

                    @Override
                    public int getBatchSize() {
                        return film.getGenres().size();
                    }
                });*/

        jdbcTemplate.batchUpdate(
                " MERGE INTO FILM_GENRE USING (SELECT ZERO() + (SELECT GENREID FROM FILM_GENRE WHERE GENREID = ?" +
                        " AND FILMID = ?) AS b) As t ON (t.b IS NOT NULL ) WHEN NOT MATCHED THEN INSERT(FILMID, GENREID) VALUES (?, ?);",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setLong(1, film.getGenres().get(i).getId());
                        ps.setLong(2, film.getId());
                        ps.setLong(3, film.getId());
                        ps.setLong(4, film.getGenres().get(i).getId());
                    }

                    @Override
                    public int getBatchSize() {
                        return film.getGenres().size();
                    }
                }
        );
    }
}
