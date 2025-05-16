package ru.practicum.shareit.item;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Marker;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@RestController
@RequestMapping("/items")
@Validated
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> get(@NotNull @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("-----------------------------------------|get|------");
        log.info("I-C -> get(X-Sharer-User-Id: {})",userId);
        return itemService.getItems(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@NotNull @RequestHeader("X-Sharer-User-Id") long userId,
                           @PathVariable(name = "itemId") long itemId) {
        log.info("-----------------------------------------|getById|------");
        log.info("I-C -> getById(X-Sharer-User-Id: {},itemId: {})",userId,itemId);
        return itemService.getByUserIdAndItemId(userId,itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> getBySearch(@NotNull @RequestHeader("X-Sharer-User-Id") long userId,
                                 @RequestParam(name = "text", required = false) String text) {
        log.info("-----------------------------------------|getBySearch|------");
        log.info("I-C -> getBySearch(X-Sharer-User-Id: {},text: {})",userId,text);
        return itemService.getBySearch(userId,text);
    }

    @PostMapping
    public ItemDto add(@NotNull @RequestHeader("X-Sharer-User-Id") long userId,
                       @RequestBody ItemDto itemDto) {
        log.info("-----------------------------------------|add|------");
        log.info("I-C -> add(X-Sharer-User-Id: {},itemDto: {})",userId,itemDto);
        return itemService.addNewItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@NotNull @RequestHeader("X-Sharer-User-Id") long userId,
                          @PathVariable(name = "itemId") long itemId,
                          @RequestBody ItemDto itemDto) {
        log.info("-----------------------------------------|update|------");
        log.info("I-C -> update(X-Sharer-User-Id: {},itemId: {},itemDto: {})",userId,itemId,itemDto);
        return itemService.updateItem(userId, itemId,itemDto);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@NotNull @RequestHeader("X-Sharer-User-Id") long userId,
                           @PathVariable(name = "itemId") long itemId) {
        log.info("-----------------------------------------|deleteItem|------");
        log.info("I-C -> deleteItem(X-Sharer-User-Id: {},itemId: {})",userId,itemId);
        itemService.deleteItem(userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@Validated({Marker.OnCreate.class}) @RequestBody CommentDto commentDto,
                                 @RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable Long itemId) {
        log.info("-----------------------------------------|addComment|------");
        return itemService.createComment(commentDto, userId, itemId);
    }
}
