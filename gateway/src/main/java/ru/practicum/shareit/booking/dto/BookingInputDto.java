package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Value;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.validation.group.DefaultValidation;
import ru.practicum.shareit.validation.group.TimeComparing;
import ru.practicum.shareit.validation.validator.ValidEndDate;
import ru.practicum.shareit.validation.validator.ValidStartDate;

import java.io.Serializable;
import java.time.LocalDateTime;

@Value
@GroupSequence({BookingInputDto.class, DefaultValidation.class, TimeComparing.class})
@ValidStartDate(groups = {TimeComparing.class})
@ValidEndDate(groups = {TimeComparing.class})
public class BookingInputDto implements Serializable {
    @NotNull(groups = {DefaultValidation.class})
    Long itemId;
    @NotNull(groups = {DefaultValidation.class})
    @FutureOrPresent(groups = {DefaultValidation.class})
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-ddTHH:mm:ss")
    LocalDateTime start;
    @NotNull(groups = {DefaultValidation.class})
    @FutureOrPresent(groups = {DefaultValidation.class})
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-ddTHH:mm:ss")
    LocalDateTime end;
}
