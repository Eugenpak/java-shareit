package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookerInfoDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Override
    public List<ItemDto> getItems(long userId,Integer from,Integer size) {
        log.info("I-S -> getItems(): userId: {}",userId);
        LocalDateTime now = LocalDateTime.now().withNano(0);
        Pageable pageable = PageRequest.of(from / size, size);

        List<ItemDto> list = itemRepository.findByOwnerIdOrderByIdAsc(userId,pageable)
                .stream()
                .map(item -> {
                    BookerInfoDto last = getLastBooking(item.getId(),now);
                    BookerInfoDto next = getNextBooking(item.getId(),now);
                    return ItemMapper.toDto(item, last,next,getAllCommentsByItemId(item.getId()));
                })
                .collect(Collectors.toList());

        return list;
    }

    @Override
    public ItemDto getByUserIdAndItemId(long userId,long itemId) {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        log.info("I-S -> getByUserIdAndItemId(): userId: {},itemId: {}",userId,itemId);
        Item item = findOneByItemId(itemId);
        log.info("----> item: {}",item);
        BookerInfoDto last = null;
        BookerInfoDto next = null;
        if (item.getOwner().getId().equals(userId)) {
            log.info("----> show one: item owner_id: {}",userId);

            last = getLastBooking(itemId,now);
            next = getNextBooking(itemId,now);
        }
        log.info("----> show item other: user_id: {}",userId);
        return ItemMapper.toDto(item,last,next,getAllCommentsByItemId(itemId));
    }

    private Item findOneByUserIdAndItemId(long userId,long itemId) {
        log.info("I-S -> findOneByUserIdAndItemId(): userId: {}, itemId: {}",userId,itemId);

        Optional<Item> itemOpt = itemRepository.findByOwnerIdAndId(userId,itemId);
        if (itemOpt.isPresent()) return itemOpt.get();
        else {
            log.warn("I-S -> findOneByUserIdAndItemId(). NOT_FOUND. item-> notId!");
            throw new NotFoundException("Вещь с itemId = " + itemId + " не найден");
        }
    }

    private Item findOneByItemId(long itemId) {
        log.info("I-S -> findOneByItemId(): itemId: {}",itemId);

        Optional<Item> itemOpt = itemRepository.findById(itemId);
        if (itemOpt.isPresent()) return itemOpt.get();
        else throw new NotFoundException("Вещь с itemId = " + itemId + " не найден");
    }

    @Override
    public List<ItemDto> getBySearch(long userId,String text,Integer from,Integer size) {
        log.info("I-S -> getBySearch(): text = {}",text);
        Pageable pageable = PageRequest.of(from / size, size);

        List<Item> list = itemRepository.getBySearch(text, pageable);
        log.info("Поиск вещи потенциальным арендатором по тексту = '{}' выполнен",text);
        return ItemMapper.toDtos(list);
    }

    @Override
    @Transactional
    public ItemDto addNewItem(long userId,ItemDto itemDto) {
        log.info("I-S -> addNewItem(): userId: {},itemDto: {}",userId,itemDto);
        UserDto userDto = userService.findUserById(userId);

        Item item = itemRepository.save(ItemMapper.fromDto(itemDto,userDto));
        log.info("Создана вещь {}",item);
        return ItemMapper.toDto(item);
    }

    @Override
    @Transactional
    public ItemDto updateItem(long userId,long itemId,ItemDto itemDto) {
        log.info("I-S -> updateItem(X-Sharer-User-Id: {}, itemId: {}, itemDto: {}) begin.",userId,itemId,itemDto);
        userService.findUserById(userId);
        Item oldItem = findOneByUserIdAndItemId(userId,itemId);
        log.info("I-S -> updateItem(). oldItem: {}",oldItem);
        if (oldItem.getOwner().getId() != userId) {
            log.warn("I-S -> updateItem(). userId({}) не равно ownerId({})",userId,oldItem.getOwner().getId());
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
        if (itemDto.getRequestId() != null) {
            log.info("I-S -> updateItem(): itemDto.RequestId = {}",itemDto.getRequestId());
            oldItem.setRequestId(itemDto.getRequestId());
        }

        Item itemUpdate = itemRepository.save(oldItem);
        log.info("Информация об вещи с itemId = {} обновлено",oldItem.getId());
        return ItemMapper.toDto(itemUpdate);
    }

    @Override
    @Transactional
    public void deleteItem(long userId,long itemId) {
        log.info("I-S -> deleteItem(): userId: {},itemId: {}",userId,itemId);
        findOneByUserIdAndItemId(userId,itemId);
        itemRepository.deleteById(itemId);
        log.info("Вещь с itemId = {} удален",itemId);
    }

    @Override
    public ItemDto findByItemId(long itemId) {
        log.info("I-S -> findByItemId(): itemId: {}",itemId);
        Optional<Item> itemOpt = itemRepository.findById(itemId);
        if (itemOpt.isPresent()) return ItemMapper.toDto(itemOpt.get());
        else {
            log.warn("I-S -> findByItemId(). NOT_FOUND. item-> notId!");
            throw new NotFoundException("Вещь с itemId = " + itemId + " не найден");
        }
    }

    @Override
    @Transactional
    public CommentDto createComment(CommentDto commentDto, long userId, long itemId) {
        log.info("I-S -> createComment(): userId: {},itemDto: {}",userId,itemId);
        LocalDateTime now = LocalDateTime.now().withNano(0);
        UserDto userDto = userService.findUserById(userId);
        ItemDto itemDto = findByItemId(itemId);
        Comment comment = CommentMapper.toComment(commentDto, userDto, itemDto);
        List<Booking> bookings = bookingRepository.getAllUserBookings(userId, itemId, now);
        log.info("I-S -> createComment(): userId: {},itemDto: {}",userId,itemId);
        if (bookings.isEmpty()) {
            log.warn("I-S -> createComment(). BAD_REQUEST. booking-> isEmpty!");
            throw new ValidationException("Создай бронирование, чтобы оставить комментарий!");
        }

        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public List<CommentDto> getAllCommentsByItemId(Long itemId) {
        log.info("I-S -> getAllCommentsByItemId(): itemId: {}",itemId);
        List<Comment> comments = commentRepository.findAllByItemId(itemId);
        return comments
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    private BookerInfoDto getLastBooking(long itemId,LocalDateTime now) {
        return bookingRepository
                .findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(itemId, Status.APPROVED,now)
                .map(BookingMapper::toBookingInfoDto)
                .orElse(null);
    }

    private BookerInfoDto getNextBooking(long itemId,LocalDateTime now) {
        return bookingRepository
                .findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(itemId, Status.APPROVED,now)
                .map(BookingMapper::toBookingInfoDto)
                .orElse(null);
    }
}
