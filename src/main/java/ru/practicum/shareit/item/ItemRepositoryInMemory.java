package ru.practicum.shareit.item;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Repository
@Slf4j
@NoArgsConstructor
public class ItemRepositoryInMemory implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();

    private long getNextId() {
        long currentMaxId = items.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @Override
    public List<Item> findAll(long userId) {
        log.debug("ItemRepositoryInMemory findAll().");
        return items.values().stream().filter(i -> i.getOwnerId() == userId).toList();
    }

    @Override
    public Item create(Item item) {
        log.debug("ItemRepositoryInMemory create(item:{}).", item);
        item.setId(getNextId());
        items.put(item.getId(),item);
        return item;
    }

    @Override
    public Item update(Item item) {
        log.debug("ItemRepositoryInMemory update(item:{}).", item);
        items.put(item.getId(),item);
        return item;
    }

    @Override
    public Optional<Item> findItemById(long itemId) {
        log.debug("ItemRepositoryInMemory findItemById(id:{}).", itemId);
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public boolean delItemById(long itemId) {
        log.debug("ItemRepositoryInMemory delUserById(itemId:{}).", itemId);
        Item item = items.remove(itemId);
        return item != null;
    }

    @Override
    public List<Item> getBySearch(String text) {
        List<Item> list = items.values().stream().filter(Item::getAvailable)
                .filter(i -> searchSubStr(i,text)).toList();
        log.debug("ItemRepositoryInMemory getBySearch(text:{}).", text);
        return list;
    }

    private boolean searchSubStr(Item itemSearch, String text) {
        boolean flag = itemSearch.getName().toLowerCase().contains(text.toLowerCase());
        if (flag) return true;
        flag = itemSearch.getDescription().toLowerCase().contains(text.toLowerCase());
        return flag;
    }
}
