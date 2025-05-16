package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Value;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.Marker;

import java.time.LocalDateTime;

@Value
public class BookingInDto {
    @NotNull(groups = {Marker.OnCreate.class})
    Long itemId;
    @NotNull(groups = {Marker.OnCreate.class})
    @DateTimeFormat(pattern = "yyyy-MM-ddTHH:mm:ss")
    LocalDateTime start;
    @NotNull(groups = {Marker.OnCreate.class})
    @DateTimeFormat(pattern = "yyyy-MM-ddTHH:mm:ss")
    LocalDateTime end;
}
