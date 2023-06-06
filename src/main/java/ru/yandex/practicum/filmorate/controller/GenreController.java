package ru.yandex.practicum.filmorate.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController("/genres")
public class GenreController {
    private final FilmService filmService;

    @Autowired
    public GenreController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/genres")
    public List<Genre> getGenres() {
        return filmService.getGenres();
    }

    @GetMapping ("/genres/{id}")
    public Genre getGenreById(@PathVariable Long id) {
        return filmService.getGenreById(id);
    }
}
