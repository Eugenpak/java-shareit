package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> getItems(long userId);

    ItemDto getByItemId(long userId,long itemId);

    List<ItemDto> getBySearch(long userId,String text);

    ItemDto addNewItem(long userId,ItemDto itemDto);

    ItemDto updateItem(long userId,long itemId,ItemDto itemDto);

    void deleteItem(long userId,long itemId);
}
