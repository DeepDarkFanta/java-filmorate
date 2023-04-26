package ru.yandex.practicum.filmorate.util.annotation;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class FutureFromValidator implements ConstraintValidator<FutureFrom, LocalDate> {
    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        return !value.isBefore(LocalDate.of(1895,12,28));
    }
}
