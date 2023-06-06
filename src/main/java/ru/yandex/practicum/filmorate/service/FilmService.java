package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaWithName;
import ru.yandex.practicum.filmorate.storage.FilmDBStorage;

import java.util.List;

@Service
public class FilmService {
    private final FilmDBStorage filmDBStorage;

    private final FilmDao filmDao;

    @Autowired
    public FilmService(FilmDBStorage filmDBStorage, FilmDao filmDao) {

        this.filmDBStorage = filmDBStorage;
        this.filmDao = filmDao;
    }

    public Film addFilm(Film film) {
        return filmDBStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmDBStorage.updateFilm(film);
    }

    public List<Film> getAllFilms() {
        return filmDBStorage.getListFilms();
    }

    public Film getFilmById(Long id) {
        return filmDao.getFilmById(id);
    }

    public void likeFilm(Long id, Long userId) {
        filmDao.likeFilm(id, userId);
    }

    public void deleteLike(Long id, Long userId) {
        filmDao.deleteLike(id, userId);
    }

    public List<Film> getTopFilms(int count) {
        return filmDao.getTopFilms(count);
    }

    public List<Genre> getGenres() {
        return filmDao.getGenres();
    }

    public Genre getGenreById(Long id) {
        return filmDao.getGenreById(id);
    }

    public List<MpaWithName> getAllMpa() {
        return filmDao.getAllMpa();
    }

    public MpaWithName getMpaById(Long id) {
        return filmDao.getMpaById(id);
    }
}
