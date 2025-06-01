package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Value;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.Marker;

import java.io.Serializable;
import java.time.LocalDateTime;

@Value
public class BookingInDto implements Serializable {
    @NotNull(groups = {Marker.OnCreate.class})
    Long itemId;
    @NotNull(groups = {Marker.OnCreate.class})
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-ddTHH:mm:ss")
    LocalDateTime start;
    @NotNull(groups = {Marker.OnCreate.class})
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-ddTHH:mm:ss")
    LocalDateTime end;
}
