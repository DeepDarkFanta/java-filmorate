package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.util.annotation.FutureFrom;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class Film {
    private int id;

    @NotBlank(message = "name cannot be empty")
    private String name;

    @Size(max = 200, message = "Description exceeds 200 characters")
    private String description;

    @FutureFrom
    private LocalDate releaseDate;

    @Positive
    private int duration;

    //как лучше это проинициализировать? Пробовал через спринг но он не находит бин сета...
    private final Set<Integer> likes = new HashSet<>();
}
