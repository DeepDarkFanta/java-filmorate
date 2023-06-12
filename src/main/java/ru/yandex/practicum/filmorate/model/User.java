package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class User {
    private long id;

    private Set<Integer> friends;

    @NotBlank(message = "email cannot be empty")
    @Email(message = "must contain the @ symbol")
    private String email;

    @NotBlank(message = "login cannot be empty and contain spaces")
    private String login;

    private String name;

    @Past(message = "date of birth cannot be in the future")

    private LocalDate birthday;
}
