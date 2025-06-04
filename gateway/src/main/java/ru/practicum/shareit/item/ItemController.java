package ru.practicum.shareit.item;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.validation.group.Create;
import ru.practicum.shareit.validation.group.Update;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                         @Validated({Create.class}) @RequestBody ItemDto itemDto) {
        return itemClient.create(itemDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                         @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
                                         @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer size) {
        return itemClient.getAllByUserId(userId, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@Validated({Create.class}) @RequestBody CommentDto commentDto,
                                             @RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                             @PathVariable @Positive Long itemId) {
        return itemClient.addComment(commentDto, userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                          @PathVariable("itemId") Long itemId) {
        return itemClient.getByItemIdAndUserId(itemId, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getByText(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                            @RequestParam(name = "text") String text,
                                            @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
                                            @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer size) {
        if (text == null || text.trim().isEmpty()) {
            List<Object> emptyList = new ArrayList<>();
            return new ResponseEntity<>(emptyList, HttpStatus.OK);
        }
        return itemClient.getAllByText(userId, text, from, size);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                         @Validated({Update.class}) @RequestBody ItemDto itemDto,
                                         @PathVariable("itemId") @Positive Long itemId) {
        return itemClient.update(userId, itemId, itemDto);
    }
}
