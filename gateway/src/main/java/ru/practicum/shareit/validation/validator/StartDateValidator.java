package ru.practicum.shareit.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.practicum.shareit.booking.dto.BookingInputDto;

public class StartDateValidator  implements ConstraintValidator<ValidStartDate, BookingInputDto> {
    @Override
    public boolean isValid(BookingInputDto value, ConstraintValidatorContext context) {
        return !value.getStart().isEqual(value.getEnd());
    }
}