package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MpaWithName;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;
@RestController("/mpa")
public class MpaController {
    private final FilmService filmService;

    @Autowired
    public MpaController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/mpa")
    public List<MpaWithName> getAllMpa() {
        return filmService.getAllMpa();
    }

    @GetMapping("/mpa/{id}")
    public MpaWithName getMpaById(@PathVariable Long id) {
        return filmService.getMpaById(id);
    }
}
