package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.Marker;

import java.time.LocalDateTime;

@Value
public class CommentDto {
    Long id;
    @NotBlank(groups = {Marker.OnCreate.class})
    String text;
    String authorName;
    @DateTimeFormat(pattern = "yyyy-MM-ddTHH:mm:ss")
    LocalDateTime created;
}
