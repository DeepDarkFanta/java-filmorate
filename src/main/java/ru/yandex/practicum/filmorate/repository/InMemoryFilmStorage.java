package ru.yandex.practicum.filmorate.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.util.exception.ValidationException;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films;

    private final AtomicInteger id;

    @Autowired
    public InMemoryFilmStorage() {
        this.films = new HashMap<>();
        this.id = new AtomicInteger();
    }

    @Override
    public Film addFilm(Film film) {
        film.setId(id.incrementAndGet());
        films.put(id.get(), film);
        log.info("Film has been added");
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.info("Film has been updated");
        } else {
            throw new ValidationException("There is no such ID film");
        }
        return film;
    }

    @Override
    public List<Film> getListFilms() {
        return new ArrayList<>(films.values());
    }
}
