package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.util.annotation.FutureFrom;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@Builder
public class Film {
    private int id;

    @NotNull(message = "name cannot be empty")
    @NotBlank(message = "name cannot be empty")
    private String name;

    @Size(max = 200, message = "Description exceeds 200 characters")
    private String description;

    @FutureFrom
    private LocalDate releaseDate;

    @Positive
    private int duration;
}
