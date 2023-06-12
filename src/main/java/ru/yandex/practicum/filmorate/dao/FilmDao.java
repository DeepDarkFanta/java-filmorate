package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.*;

import java.util.List;

public interface FilmDao {
    Film getFilmById(Long filmId);

    List<Genres> getGenresByFilmId(Long filmId);

    void likeFilm(Long id, Long userId);

    void deleteLike(Long id, Long userId);

    List<Film> getTopFilms(int count);

    List<Genre> getGenres();

    Genre getGenreById(Long id);

    List<MpaWithName>  getAllMpa();

    MpaWithName getMpaById(Long id);

    MpaWithName getMpaByFilmId(Long filmId);
}
