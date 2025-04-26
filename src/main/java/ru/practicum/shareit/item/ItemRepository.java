package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;
import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    boolean delItemById(long itemId);
    Optional<Item> findItemById(long itemId);
    Item update(Item item);
    Item create(Item item);
    List<Item> findAll(long userId);
    List<Item> getBySearch(String text);
}
