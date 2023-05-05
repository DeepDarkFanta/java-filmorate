package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.repository.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.util.exception.ValidationException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final InMemoryFilmStorage inMemoryFilmStorage;
    private final InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage inMemoryFilmStorage, InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public Film addFilm(Film film) {
        return inMemoryFilmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return inMemoryFilmStorage.updateFilm(film);
    }

    public List<Film> getAllFilms() {
        return inMemoryFilmStorage.getListFilms();
    }

    public Film getFilmById(int id) {
        return inMemoryFilmStorage.getListFilms().stream()
                .filter(x -> x.getId() == id)
                .findFirst()
                .orElseThrow(() -> new ValidationException("There is no such ID film"));
    }

    public void likeFilm(int id, int userId) {
        Film film = getFilmById(id);
        inMemoryUserStorage.checkUserById(userId);
        film.getLikes().add(userId);
    }

    public void deleteLike(int id, int userId) {
        Film film = getFilmById(id);
        inMemoryUserStorage.checkUserById(userId);
        film.getLikes().remove(userId);
    }

    public List<Film> getTopFilms(int count) {
        List<Film> topFilms = inMemoryFilmStorage.getListFilms();
        topFilms.sort((x1, x2) -> Integer.compare(x2.getLikes().size(), x1.getLikes().size()));
        if (topFilms.size() < count) return topFilms;
        return topFilms.stream()
                .limit(count)
                .collect(Collectors.toList());
    }
}
