package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class User {

    private int id;

    @Email(message = "email cannot be empty and must contain the @ symbol")
    private String email;

    @NotBlank(message = "login cannot be empty and contain spaces")
    private String login;

    private String name;

    @Future(message = "date of birth cannot be in the future")
    private LocalDate birthday;
}
