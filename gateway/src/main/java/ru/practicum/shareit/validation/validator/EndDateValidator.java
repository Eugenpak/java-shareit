package ru.practicum.shareit.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.practicum.shareit.booking.dto.BookingInputDto;

class EndDateValidator implements ConstraintValidator<ValidEndDate, BookingInputDto> {
    @Override
    public boolean isValid(BookingInputDto value, ConstraintValidatorContext context) {
        return value.getStart().isBefore(value.getEnd());
    }
}
