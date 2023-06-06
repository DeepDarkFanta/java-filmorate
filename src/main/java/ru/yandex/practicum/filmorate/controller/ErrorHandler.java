package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.util.exception.UserAndFilmErrorResponse;
import ru.yandex.practicum.filmorate.util.exception.ValidationException;
import java.util.stream.Collectors;

@RestControllerAdvice("ru.yandex.practicum.filmorate.controller")
@Slf4j
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private UserAndFilmErrorResponse handleValidationException(MethodArgumentNotValidException e) {
        log.warn(e.getMessage());

         return new UserAndFilmErrorResponse(
                 e.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList())
                .toString()
         );
    }
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private UserAndFilmErrorResponse handleNotFoundException(ValidationException e) {
        log.warn(e.getMessage());
        return new UserAndFilmErrorResponse(e.getMessage());
    }
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private UserAndFilmErrorResponse handleNotFoundExceptionInBase(IndexOutOfBoundsException e) {
        log.warn(e.getMessage());
        return new UserAndFilmErrorResponse(e.getMessage());
    }
}

