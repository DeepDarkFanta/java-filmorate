package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaWithName;
import ru.yandex.practicum.filmorate.storage.FilmDBStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.List;

@Service
public class FilmService {
    private final InMemoryFilmStorage inMemoryFilmStorage;
    private final InMemoryUserStorage inMemoryUserStorage;
    private final FilmDBStorage filmDBStorage;
    private final FilmDao filmDao;

    @Autowired
    public FilmService(InMemoryFilmStorage inMemoryFilmStorage, InMemoryUserStorage inMemoryUserStorage, FilmDBStorage filmDBStorage, FilmDao filmDao) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
        this.inMemoryUserStorage = inMemoryUserStorage;
        this.filmDBStorage = filmDBStorage;
        this.filmDao = filmDao;
    }

    public Film addFilm(Film film) {
        return filmDBStorage.addFilm(film);
        /*return inMemoryFilmStorage.addFilm(film);*/
    }

    public Film updateFilm(Film film) {
        return filmDBStorage.updateFilm(film);
    }

    public List<Film> getAllFilms() {
        return filmDBStorage.getListFilms();
    }

    public Film getFilmById(Long id) {
        return filmDao.getFilmById(id);
        /*return inMemoryFilmStorage.getListFilms().stream()
                .filter(x -> x.getId() == id)
                .findFirst()
                .orElseThrow(() -> new ValidationException("There is no such ID film"));*/
    }

    public void likeFilm(Long id, Long userId) {
        filmDao.likeFilm(id, userId);
        /*Film film = getFilmById(id);
        inMemoryUserStorage.checkUserById(userId);
        film.getLikes().add(userId);*/
    }

    public void deleteLike(Long id, Long userId) {
        filmDao.deleteLike(id, userId);
        /*Film film = getFilmById(id);
        inMemoryUserStorage.checkUserById(userId);
        film.getLikes().remove(userId);*/
    }

    public List<Film> getTopFilms(int count) {
        return filmDao.getTopFilms(count);
        /*List<Film> topFilms = inMemoryFilmStorage.getListFilms();
        topFilms.sort((x1, x2) -> Integer.compare(x2.getLikes().size(), x1.getLikes().size()));
        if (topFilms.size() < count) return topFilms;
        return topFilms.stream()
                .limit(count)
                .collect(Collectors.toList());*/
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
