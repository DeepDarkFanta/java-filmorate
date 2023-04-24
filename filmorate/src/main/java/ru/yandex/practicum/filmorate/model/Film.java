package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.filmorate.util.annotation.FutureFrom;
import ru.yandex.practicum.filmorate.util.annotation.FutureFromValidator;

import javax.validation.Constraint;
import javax.validation.constraints.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;


@Data
@AllArgsConstructor
public class Film {
    private int id;

    @NotNull(message = "name cannot be empty")
    @NotBlank(message = "name cannot be empty")
    private String name;

    @Max(value = 200, message = "Description exceeds 200 characters")
    private String description;

    @FutureFrom()
    private LocalDate releaseDate;

    @Positive
    private int duration;
}
