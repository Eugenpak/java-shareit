package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final ItemMapper itemMapper;

    @Override
    public List<ItemDto> getItems(long userId) {
        List<Item> list = itemRepository.findAll(userId);
        return itemMapper.toDtos(list);
    }

    @Override
    public ItemDto getByItemId(long userId,long itemId) {
        log.info("I-S -> getByItemId(): userId: {},itemId: {}",userId,itemId);
        return itemMapper.toDto(findOneByItemId(itemId));
    }

    private Item findOneByItemId(long itemId) {
        Optional<Item> itemOpt = itemRepository.findItemById(itemId);
        if (itemOpt.isPresent()) return itemOpt.get();
        else throw new NotFoundException("Вещь с itemId = " + itemId + " не найден");
    }

    @Override
    public List<ItemDto> getBySearch(long userId,String text) {
        log.info("I-S -> getBySearch(): text = {}",text);
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        List<Item> list = itemRepository.getBySearch(text);
        log.info("Поиск вещи потенциальным арендатором по тексту = '{}' выполнен",text);
        return itemMapper.toDtos(list);
    }

    @Override
    public ItemDto addNewItem(long userId,ItemDto itemDto) {
        log.info("I-S -> addNewItem(): userId: {},itemDto: {}",userId,itemDto);
        userService.findUserById(userId);
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            log.info("I-S -> addNewItem(): itemDto.name = {}",itemDto.getName());
            throw new ValidationException("Поле 'name' не должно быть пустым!");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            log.info("I-S -> addNewItem(): itemDto.description = {}",itemDto.getDescription());
            throw new ValidationException("Поле 'description' не должно быть пустым!");
        }
        if (itemDto.getAvailable() == null) {
            log.info("I-S -> addNewItem(): itemDto.Available = {}",itemDto.getAvailable());
            throw new ValidationException("Поле 'available' не должно быть пустым!");
        }
        Item item = itemRepository.create(itemMapper.fromDto(itemDto,userId));
        log.info("Создана вещь {}",item);
        return itemMapper.toDto(item);
    }

    @Override
    public ItemDto updateItem(long userId,long itemId,ItemDto itemDto) {
        log.info("I-S -> updateItem(X-Sharer-User-Id: {}, itemId: {}, itemDto: {}) begin.",userId,itemId,itemDto);
        userService.findUserById(userId);
        Item oldItem = findOneByItemId(itemId);
        log.info("I-S -> updateItem(). oldItem: {}",oldItem);
        if (oldItem.getOwnerId() != userId) {
            log.warn("I-S -> updateItem(). userId({}) yне равно ownerId({})",userId,oldItem.getOwnerId());
            throw new NotFoundException("Нет доступных вещей для обновления");
        }
        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            log.info("I-S -> updateItem(): itemDto.name = {}",itemDto.getName());
            oldItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            log.info("I-S -> updateItem(): itemDto.description = {}",itemDto.getDescription());
            oldItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            log.info("I-S -> updateItem(): itemDto.Available = {}",itemDto.getAvailable());
            oldItem.setAvailable(itemDto.getAvailable());
        }
        Item itemUpdate = itemRepository.update(oldItem);
        log.info("Информация об вещи с itemId = {} обновлено",oldItem.getId());
        return itemMapper.toDto(itemUpdate);
    }

    public void deleteItem(long userId,long itemId) {
        itemRepository.delItemById(itemId);
        log.info("Вещь с itemId = {} удален",itemId);
    }
}
