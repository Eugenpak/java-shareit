package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import org.junit.jupiter.api.Assertions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserService userService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BookingRepository bookingRepository;

    private final UserDto userDto = new UserDto(
            1L,
            "marek@mail.ru",
            "Marek");

    private final User user = new User(
            1L,
            "marek@mail.ru",
            "Marek");

    private final Item item = new Item(
            1L,
            "Какая-то вещь",
            "Какое-то описание",
            user,
            true,
            1L);

    private final Comment comment = new Comment(
            1L,
            "Какой-то текст",
            item,
            user,
            LocalDateTime.now().withNano(0));

    private final CommentDto commentDto = new CommentDto(
            null,
            "Какой-то текст",
            "Marek",
            LocalDateTime.now().withNano(0));

    private final ItemDto itemDto = new ItemDto(
            1L,
            "Какая-то вещь",
            "Какое-то описание",
            true,
            1L,
            null,
            null,
            new ArrayList<>(),
            1L);


    private final Booking booking = new Booking(
            1L,
            LocalDateTime.now(),
            LocalDateTime.now(),
            item,
            user,
            Status.WAITING);

    private final BookingOutDto bookingOutputDto = new BookingOutDto(
            1L,
            LocalDateTime.now(),
            LocalDateTime.now(),
            Status.WAITING,
            userDto,
            itemDto);

    @Test
    void createItem() {
        when(userService.findUserById(userDto.getId())).thenReturn(userDto);
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto createdItem = itemService.addNewItem(userDto.getId(),itemDto);

        Assertions.assertNotNull(createdItem);
        Assertions.assertEquals(1, createdItem.getId());
        Assertions.assertEquals(itemDto.getName(), createdItem.getName());
        Assertions.assertEquals(itemDto.getDescription(), createdItem.getDescription());
        Assertions.assertTrue(createdItem.getAvailable());
        Assertions.assertEquals(itemDto.getOwnerId(), createdItem.getOwnerId());
        Assertions.assertNull(createdItem.getLastBooking());
        Assertions.assertNull(createdItem.getNextBooking());
        Assertions.assertEquals(itemDto.getComments().size(), createdItem.getComments().size());
        Assertions.assertEquals(itemDto.getRequestId(), createdItem.getRequestId());

        verify(userService, times(1)).findUserById(anyLong());
        verify(itemRepository, times(1)).save(any(Item.class));
        verifyNoMoreInteractions(userService,itemRepository);
    }

    @Test
    void getByUserIdAndItemId() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        LocalDateTime now = LocalDateTime.now().withNano(0);
        when(bookingRepository
                .findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(item.getId(),
                        Status.APPROVED,now)).thenReturn(Optional.of(booking));
        when(bookingRepository
                .findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(item.getId(),
                        Status.APPROVED,now)).thenReturn(Optional.of(booking));
        when(commentRepository.findAllByItemId(item.getId())).thenReturn(new ArrayList<>());

        ItemDto itemById = itemService.getByUserIdAndItemId(1L, 1L);

        Assertions.assertNotNull(itemById);
        Assertions.assertEquals(1, itemById.getId());
        Assertions.assertEquals(itemDto.getName(), itemById.getName());
        Assertions.assertEquals(itemDto.getDescription(), itemById.getDescription());
        Assertions.assertTrue(itemById.getAvailable());
        Assertions.assertEquals(itemDto.getOwnerId(), itemById.getOwnerId());
        Assertions.assertEquals(bookingOutputDto.getId(), itemById.getLastBooking().getId());
        Assertions.assertEquals(bookingOutputDto.getId(), itemById.getNextBooking().getId());
        Assertions.assertEquals(0, itemById.getComments().size());
        Assertions.assertEquals(itemDto.getRequestId(), itemById.getRequestId());

        verify(itemRepository, times(1)).findById(1L);

        verify(bookingRepository, times(1))
                .findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(item.getId(),
                        Status.APPROVED,now);
        verify(bookingRepository, times(1))
                .findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(item.getId(),
                        Status.APPROVED,now);
        verify(commentRepository, times(1)).findAllByItemId(item.getId());
        verifyNoMoreInteractions(itemRepository,bookingRepository,commentRepository);
    }

    @Test
    void getByUserIdAndItemIdNotFoundItem() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class,
                () -> itemService.getByUserIdAndItemId(1L, 1L));

        verify(itemRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void findByItemId() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ItemDto itemById = itemService.findByItemId(itemDto.getId());

        Assertions.assertNotNull(itemById);
        Assertions.assertEquals(1, itemById.getId());
        Assertions.assertEquals(itemDto.getName(), itemById.getName());
        Assertions.assertEquals(itemDto.getDescription(), itemById.getDescription());
        Assertions.assertTrue(itemById.getAvailable());
        Assertions.assertEquals(itemDto.getOwnerId(), itemById.getOwnerId());
        Assertions.assertEquals(itemDto.getRequestId(), itemById.getRequestId());

        verify(itemRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void findByItemIdNotFoundItem() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class,
                () -> itemService.findByItemId(itemDto.getId()));

        verify(itemRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void getByUserIdAndItemIdWithoutBookings() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(item.getId())).thenReturn(new ArrayList<>());

        ItemDto itemById = itemService.getByUserIdAndItemId(2L, 1L);

        Assertions.assertNotNull(itemById);
        Assertions.assertEquals(1, itemById.getId());
        Assertions.assertEquals(itemDto.getName(), itemById.getName());
        Assertions.assertEquals(itemDto.getDescription(), itemById.getDescription());
        Assertions.assertTrue(itemById.getAvailable());
        Assertions.assertEquals(itemDto.getOwnerId(), itemById.getOwnerId());
        Assertions.assertNull(itemById.getLastBooking());
        Assertions.assertNull(itemById.getNextBooking());
        Assertions.assertEquals(0, itemById.getComments().size());
        Assertions.assertEquals(itemDto.getRequestId(), itemById.getRequestId());

        verify(itemRepository, times(1)).findById(1L);
        verify(commentRepository, times(1)).findAllByItemId(item.getId());
        verifyNoMoreInteractions(itemRepository,commentRepository);
    }

    @Test
    void getAllItemsByUserId() {
        when(itemRepository.findByOwnerIdOrderByIdAsc(anyLong(), any(Pageable.class))).thenReturn(List.of(item));
        LocalDateTime now = LocalDateTime.now().withNano(0);
        when(bookingRepository
                .findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(item.getId(),
                        Status.APPROVED,now)).thenReturn(Optional.of(booking));
        when(bookingRepository
                .findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(item.getId(),
                        Status.APPROVED,now)).thenReturn(Optional.of(booking));
        when(commentRepository.findAllByItemId(item.getId())).thenReturn(new ArrayList<>());

        List<ItemDto> items = itemService.getItems(1L, 0, 1);

        Assertions.assertEquals(items.size(), 1);
        Assertions.assertEquals(1, items.get(0).getId());
        Assertions.assertEquals(itemDto.getName(), items.get(0).getName());
        Assertions.assertEquals(itemDto.getDescription(), items.get(0).getDescription());
        Assertions.assertTrue(items.get(0).getAvailable());
        Assertions.assertEquals(itemDto.getOwnerId(), items.get(0).getOwnerId());
        Assertions.assertEquals(bookingOutputDto.getId(), items.get(0).getLastBooking().getId());
        Assertions.assertEquals(bookingOutputDto.getId(), items.get(0).getNextBooking().getId());
        Assertions.assertEquals(0, items.get(0).getComments().size());
        Assertions.assertEquals(itemDto.getRequestId(), items.get(0).getRequestId());

        verify(itemRepository, times(1)).findByOwnerIdOrderByIdAsc(anyLong(), any(Pageable.class));
        verifyNoMoreInteractions(itemRepository);
        verify(bookingRepository, times(1))
                .findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(item.getId(),
                        Status.APPROVED,now);
        verify(bookingRepository, times(1))
                .findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(item.getId(),
                        Status.APPROVED,now);
        verify(commentRepository, times(1)).findAllByItemId(item.getId());
        verifyNoMoreInteractions(bookingRepository,commentRepository);
    }

    @Test
    void getBySearch() {
        when(itemRepository.getBySearch(anyString(), any(Pageable.class))).thenReturn(List.of(item));

        List<ItemDto> items = itemService.getBySearch(1L,"Hello", 0, 1);

        Assertions.assertEquals(1, items.size());
        Assertions.assertEquals(1, items.get(0).getId());
        Assertions.assertEquals(itemDto.getName(), items.get(0).getName());
        Assertions.assertEquals(itemDto.getDescription(), items.get(0).getDescription());
        Assertions.assertTrue(items.get(0).getAvailable());
        Assertions.assertEquals(itemDto.getOwnerId(), items.get(0).getOwnerId());
        Assertions.assertEquals(itemDto.getRequestId(), items.get(0).getRequestId());

        verify(itemRepository, times(1)).getBySearch(anyString(), any(Pageable.class));
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void getAllItemsByEmptyText() {
        List<ItemDto> items = itemService.getBySearch(1L,"", 0, 1);

        Assertions.assertEquals(items.size(), 0);
    }

    @Test
    void updateItem() {
        final ItemDto newItemDto = new ItemDto(
                1L,
                "Какая-то обновленная вещь",
                "Какое-то описание",
                true,
                1L,
                null,
                null,
                new ArrayList<>(),
                1L);

        when(userService.findUserById(1L)).thenReturn(userDto);
        when(itemRepository.findByOwnerIdAndId(1L, 1L)).thenReturn(Optional.of(item));
        when(itemRepository.save(item)).thenReturn(item);

        ItemDto updatedItem = itemService.updateItem(1L, 1L, newItemDto);

        Assertions.assertNotNull(updatedItem);
        Assertions.assertEquals(1, updatedItem.getId());
        Assertions.assertEquals(newItemDto.getName(), updatedItem.getName());
        Assertions.assertEquals(itemDto.getDescription(), updatedItem.getDescription());
        Assertions.assertTrue(updatedItem.getAvailable());
        Assertions.assertEquals(itemDto.getOwnerId(), updatedItem.getOwnerId());
        Assertions.assertEquals(itemDto.getRequestId(), updatedItem.getRequestId());

        verify(userService, times(1)).findUserById(1L);
        verify(itemRepository, times(1)).findByOwnerIdAndId(1L, 1L);
        verify(itemRepository, times(1)).save(item);
        verifyNoMoreInteractions(userService,itemRepository);
    }

    @Test
    void updateWithNotFoundItem() {
        Long userId = 1L;
        Long itemId = 1L;

        //when(itemRepository.findByOwnerIdAndId(anyLong(), anyLong())).thenReturn(null);
        when(userService.findUserById(userId)).thenReturn(userDto);
        when(itemRepository.findByOwnerIdAndId(userId, itemId)).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class,
                () -> itemService.updateItem(userId, itemId, itemDto));
        verify(userService, times(1)).findUserById(userId);
        verifyNoMoreInteractions(userService);
    }

    @Test
    void updateWithNotFoundAvailableItem() {
        Long userId = 2L;
        Long itemId = 1L;

        when(userService.findUserById(userId)).thenReturn(userDto);
        when(itemRepository.findByOwnerIdAndId(userId, itemId)).thenReturn(Optional.of(item));

        Assertions.assertThrows(NotFoundException.class,
                () -> itemService.updateItem(userId, itemId, itemDto));
        verify(userService, times(1)).findUserById(userId);
        verify(itemRepository, times(1)).findByOwnerIdAndId(userId, itemId);
        verifyNoMoreInteractions(userService,itemRepository);
    }

    @Test
    void createComment() {
        LocalDateTime created = LocalDateTime.now().withNano(0);

        CommentDto commentDtoCreated = new CommentDto(null,"Какой-то текст","Marek",created);
        Comment commentSave = new Comment(1L,"Какой-то текст",item,user,created);

        when(userService.findUserById(userDto.getId())).thenReturn(userDto);
        when(itemRepository.findById(itemDto.getId())).thenReturn(Optional.of(item));
        LocalDateTime now = LocalDateTime.now().withNano(0);
        when(bookingRepository.getAllUserBookings(userDto.getId(), itemDto.getId(), now)).thenReturn(List.of(booking));
        when(commentRepository.save(any(Comment.class))).thenReturn(commentSave);

        CommentDto createdComment = itemService.createComment(commentDtoCreated, userDto.getId(), itemDto.getId());

        Assertions.assertNotNull(createdComment);
        Assertions.assertNotNull(createdComment.getId());
        Assertions.assertEquals(commentDtoCreated.getText(), createdComment.getText());
        Assertions.assertEquals(commentDtoCreated.getCreated().toString(), createdComment.getCreated().toString());
        Assertions.assertEquals(commentDtoCreated.getAuthorName(), createdComment.getAuthorName());

        verify(userService, times(1)).findUserById(userDto.getId());
        verify(itemRepository, times(1)).findById(itemDto.getId());
        verify(bookingRepository, times(1))
                .getAllUserBookings(userDto.getId(), itemDto.getId(), now);
        verify(commentRepository, times(1)).save(any(Comment.class));
        verifyNoMoreInteractions(userService,itemRepository,bookingRepository,commentRepository);
    }

    @Test
    void createCommentWithEmptyBookings() {
        LocalDateTime created = LocalDateTime.now().withNano(0);

        CommentDto commentDtoCreated = new CommentDto(null,"Какой-то текст","Marek",created);
        when(userService.findUserById(userDto.getId())).thenReturn(userDto);
        when(itemRepository.findById(itemDto.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.getAllUserBookings(userDto.getId(), itemDto.getId(), created))
                .thenReturn(Collections.emptyList());

        Assertions.assertThrows(ValidationException.class,
                () -> itemService.createComment(commentDtoCreated, userDto.getId(), itemDto.getId()));
    }

    @Test
    void delUserById() {
        Long userId = 1L;
        Long itemId = 1L;
        when(itemRepository.findByOwnerIdAndId(userId,itemId)).thenReturn(Optional.of(item));

        itemService.deleteItem(userId,itemId);

        verify(itemRepository, times(1)).findByOwnerIdAndId(userId,itemId);
        verify(itemRepository, times(1)).deleteById(itemId);
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void createItemNameIsBlank() {
        when(userService.findUserById(userDto.getId())).thenReturn(userDto);
        itemDto.setName("");

        Assertions.assertThrows(ValidationException.class,
                () -> itemService.addNewItem(userDto.getId(),itemDto));

        verify(userService, times(1)).findUserById(anyLong());
        verifyNoMoreInteractions(userService,itemRepository);
    }

    @Test
    void createItemDescriptionIsBlank() {
        when(userService.findUserById(userDto.getId())).thenReturn(userDto);
        itemDto.setDescription("");

        Assertions.assertThrows(ValidationException.class,
                () -> itemService.addNewItem(userDto.getId(),itemDto));

        verify(userService, times(1)).findUserById(anyLong());
        verifyNoMoreInteractions(userService,itemRepository);
    }

    @Test
    void createItemAvailableIsNull() {
        when(userService.findUserById(userDto.getId())).thenReturn(userDto);
        itemDto.setAvailable(null);

        Assertions.assertThrows(ValidationException.class,
                () -> itemService.addNewItem(userDto.getId(),itemDto));

        verify(userService, times(1)).findUserById(anyLong());
        verifyNoMoreInteractions(userService,itemRepository);
    }
}