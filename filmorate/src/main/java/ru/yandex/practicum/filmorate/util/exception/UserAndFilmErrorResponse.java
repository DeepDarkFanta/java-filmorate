package ru.yandex.practicum.filmorate.util.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class UserAndFilmErrorResponse {
    private String message;

    public UserAndFilmErrorResponse(String message) {
        this.message = "validation failed: " + message;
    }
}
