package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;
import ru.practicum.shareit.validation.group.Create;
import ru.practicum.shareit.validation.group.Update;

import java.util.List;

@Value
public class ItemDto {
    Long id;
    @NotBlank(groups = {Create.class})
    @Size(groups = {Create.class, Update.class}, min = 1)
    String name;
    @NotBlank(groups = {Create.class})
    @Size(groups = {Create.class, Update.class}, min = 1)
    String description;
    @NotNull(groups = {Create.class})
    Boolean available;
    Long ownerId;
    BookerInfoDto lastBooking;
    BookerInfoDto nextBooking;
    List<CommentDto> comments;
    Long requestId;
}
