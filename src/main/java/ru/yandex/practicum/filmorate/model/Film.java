package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.util.annotation.FutureFrom;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.*;

@Setter
@Getter
public class Film {
    private Long id;

    @NotBlank(message = "name cannot be empty")
    private String name;

    @Size(max = 200, message = "Description exceeds 200 characters")
    private String description;

    @FutureFrom
    private LocalDate releaseDate;

    @Positive
    private int duration;

    private  MpaWithName mpa;

    private Long mpaId;

    private List<Genres> genres = new ArrayList<>();
}
