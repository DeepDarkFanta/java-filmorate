package ru.yandex.practicum.filmorate.util.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ValidationException extends RuntimeException {
    private String message;

    public ValidationException() {
    }

    public ValidationException(String message) {
        log.warn(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
