package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.RequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.ArrayList;

/*
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)  */
@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
public class ItemServiceIntegrationTest {
    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private RequestService requestService;

    @Autowired
    private BookingService bookingService;

    private final UserDto userDto = new UserDto(
            null,
            "tanja@gmail.com",
            "Tanja");

    private final ItemDto itemDto = new ItemDto(
            null,
            "Какая-то вещь",
            "Какое-то описание",
            true,
            1L,
            null,
            null,
            new ArrayList<>(),
            null);

    private final ItemDto itemDto2 = new ItemDto(
            null,
            "Какая-то вещь",
            "Какое-то описание",
            true,
            2L,
            null,
            null,
            new ArrayList<>(),
            null);

    private final UserDto userDto2 = new UserDto(
            null,
            "stas@mail.ru",
            "Stas");

    private final ItemDto itemDtoToRequest = new ItemDto(
            null,
            "Какая-то вещь",
            "Какое-то описание",
            true,
            1L,
            null,
            null,
            new ArrayList<>(),
            1L);

    private final ItemRequestDto requestDto = new ItemRequestDto(
            1L,
            "Какой-то запрос",
            null,
            null);

    private final BookingInDto bookingInputDto1 = new BookingInDto(
            1L,
            LocalDateTime.now().plusMinutes(3),
            LocalDateTime.now().plusMinutes(5));

    private final CommentDto commentDto = new CommentDto(
            null,
            "Коммент",
            null,
            null);

    @Test
    void createItem() {
        UserDto createdUser = userService.create(userDto);
        ItemDto createdItem = itemService.addNewItem(createdUser.getId(), itemDto);

        Assertions.assertEquals(1L, createdItem.getId());
        Assertions.assertEquals(itemDto.getName(), createdItem.getName());
        Assertions.assertEquals(itemDto.getDescription(), createdItem.getDescription());
    }
}
