package ru.yandex.practicum.filmorate.util.annotation;

import javax.validation.Constraint;
import java.lang.annotation.*;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;

@Target({ FIELD, PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = FutureFromValidator.class)
public @interface FutureFrom {
    String message() default "Date must not be before";
    Class<?>[] groups() default {};
    Class<?>[] payload() default {};
}
