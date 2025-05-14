package ru.practicum.shareit.booking.dto;

import lombok.Value;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Value
public class BookingOutDto {
    Long id;
    @DateTimeFormat(pattern = "yyyy-MM-ddTHH:mm:ss")
    LocalDateTime start;
    @DateTimeFormat(pattern = "yyyy-MM-ddTHH:mm:ss")
    LocalDateTime end;
    Status status;
    UserDto booker;
    ItemDto item;
}
