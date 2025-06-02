package ru.practicum.shareit.item;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.validation.group.Create;
import ru.practicum.shareit.validation.group.Update;

@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @Validated({Create.class}) @RequestBody ItemDto itemDto) {
        return itemClient.create(itemDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestParam(value = "from", required = false, defaultValue = "0") @Min(0) Integer from,
                                         @RequestParam(value = "size", required = false, defaultValue = "10") @Min(1) Integer size) {
        return itemClient.getAllByUserId(userId, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@Validated({Create.class}) @RequestBody CommentDto commentDto,
                                             @RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long itemId) {
        return itemClient.addComment(commentDto, userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable("itemId") Long itemId) {
        return itemClient.getByItemIdAndUserId(itemId, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getByText(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @RequestParam(name = "text") String text,
                                            @RequestParam(value = "from", required = false, defaultValue = "0") @Min(0) Integer from,
                                            @RequestParam(value = "size", required = false, defaultValue = "10") @Min(1) Integer size) {
        return itemClient.getAllByText(userId, text, from, size);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @Validated({Update.class}) @RequestBody ItemDto itemDto,
                                         @PathVariable("itemId") Long itemId) {
        return itemClient.update(userId, itemId, itemDto);
    }
}
