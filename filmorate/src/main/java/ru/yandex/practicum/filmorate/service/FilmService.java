package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.List;

@Service
public class FilmService {
    private final FilmRepository filmRepository;

    @Autowired
    public FilmService(FilmRepository filmRepository) {
        this.filmRepository = filmRepository;
    }

    public Film addFilm(Film film) {
        return filmRepository.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmRepository.updateFilm(film);
    }

    public List<Film> getAllFilms() {
        return filmRepository.getListFilms();
    }
}
