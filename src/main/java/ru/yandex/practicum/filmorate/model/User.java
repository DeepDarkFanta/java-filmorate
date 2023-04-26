package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@Builder
public class User {
    private int id;

    @NotBlank(message = "email cannot be empty")
    @Email(message = "must contain the @ symbol")
    private String email;

    @NotBlank(message = "login cannot be empty and contain spaces")
    private String login;

    private String name;

    @Past(message = "date of birth cannot be in the future")
    private LocalDate birthday;
}