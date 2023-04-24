package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FilmRepository {
    private final HashMap<Integer, Film> films;
    private int id;

    public Film addFilm(Film film) {
        film.setId(id);
        films.put(id++, film);
        return film;
    }

    public Film updateFilm(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    public List<Film> getListFilms() {
        return new ArrayList<>(films.values());
    }
}
