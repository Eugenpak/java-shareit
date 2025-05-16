package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> getItems(long userId);

    ItemDto getByUserIdAndItemId(long userId,long itemId);

    List<ItemDto> getBySearch(long userId,String text);

    ItemDto addNewItem(long userId,ItemDto itemDto);

    ItemDto updateItem(long userId,long itemId,ItemDto itemDto);

    void deleteItem(long userId,long itemId);

    ItemDto findByItemId(long itemId);

    CommentDto createComment(CommentDto commentDto,long userId,long itemId);

    List<CommentDto> getAllCommentsByItemId(Long itemId);
}
