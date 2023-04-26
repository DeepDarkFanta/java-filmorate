package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.util.exception.ValidationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class FilmRepository {
    private final HashMap<Integer, Film> films;
    private int id;

    public Film addFilm(Film film) {
        film.setId(id);
        films.put(id++, film);
        log.info("Film has been added");
        return film;
    }

    public Film updateFilm(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.info("Film has been updated");
        } else {
            throw new ValidationException("There is no such ID film");
        }
        return film;
    }

    public List<Film> getListFilms() {
        return new ArrayList<>(films.values());
    }
}
