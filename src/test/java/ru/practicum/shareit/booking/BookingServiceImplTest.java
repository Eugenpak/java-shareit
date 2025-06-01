package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.exception.BookingStateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import org.junit.jupiter.api.Assertions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserService userService;

    @Mock
    private ItemService itemService;

    private final UserDto userDto = new UserDto(
            1L,
            "igor@gmail.dom",
            "Igor");

    private final ItemDto itemDto = new ItemDto(
            1L,
            "Какая-то вещь",
            "Какое-то описание",
            true,
            2L,
            null,
            null,
            new ArrayList<>(),
            1L);

    private final ItemDto itemEqualOwnerIdDto = new ItemDto(
            1L,
            "Какая-то вещь",
            "Какое-то описание",
            true,
            1L,
            null,
            null,
            new ArrayList<>(),
            1L);

    private final ItemDto itemDtoUnavailable = new ItemDto(
            1L,
            "Какая-то вещь",
            "Какое-то описание",
            false,
            2L,
            null,
            null,
            new ArrayList<>(),
            1L);

    private final LocalDateTime start = LocalDateTime.now().withNano(0).plusMinutes(1);
    private final LocalDateTime end = LocalDateTime.now().withNano(0).plusDays(1);

    private final BookingOutDto bookingOutDto = new BookingOutDto(
            1L,
            start,
            end,
            Status.APPROVED,
            userDto,
            itemDto);

    private final BookingInDto bookingInDto = new BookingInDto(
            1L,
            start,
            end);
    private final BookingInDto bookingInputWrongEndDateDto = new BookingInDto(
            1L,
            start,
            end.minusDays(2));

    private final BookingInDto bookingInputWringStartDateDto = new BookingInDto(
            1L,
            start.minusDays(2),
            end);

    private final BookingInDto bookingInputEqualDatesDto = new BookingInDto(
            1L,
            start,
            start);

    private final User user = new User(
            1L,
            "Igor",
            "igor@gmail.dom");

    private final Item item = new Item(
            1L,
            "Какая-то вещь",
            "Какое-то описание",
            user,
            true,
            1L);

    private final Booking booking = new Booking(
            1L,
            start,
            end,
            item,
            user,
            Status.APPROVED);

    private final Booking bookingWaiting = new Booking(
            1L,
            start,
            end,
            item,
            user,
            Status.WAITING);

    @Test
    void createBooking() {
        when(userService.findUserById(anyLong())).thenReturn(userDto);
        when(itemService.findByItemId(bookingInDto.getItemId())).thenReturn(itemDto);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingOutDto createdBooking = bookingService.create(anyLong(), bookingInDto);

        Assertions.assertNotNull(createdBooking);
        Assertions.assertEquals(bookingOutDto.getId(), createdBooking.getId());
        Assertions.assertEquals(bookingOutDto.getStart(), createdBooking.getStart());
        Assertions.assertEquals(bookingOutDto.getEnd(), createdBooking.getEnd());
        Assertions.assertEquals(bookingOutDto.getItem().getId(), createdBooking.getItem().getId());
        Assertions.assertEquals(bookingOutDto.getBooker().getId(), createdBooking.getBooker().getId());
        Assertions.assertEquals(bookingOutDto.getStatus(), createdBooking.getStatus());
        verify(userService,times(1)).findUserById(anyLong());
        verify(itemService,times(1)).findByItemId(bookingInDto.getItemId());
        verify(bookingRepository,times(1)).save(any(Booking.class));
    }

    @Test
    void createBookingUnavailable() {
        when(userService.findUserById(anyLong())).thenReturn(userDto);
        when(itemService.findByItemId(bookingInDto.getItemId())).thenReturn(itemDtoUnavailable);
        Assertions.assertThrows(ValidationException.class,
                () -> bookingService.create(userDto.getId(), bookingInDto));
    }

    @Test
    void createBookingWrongEndDate() {
        when(userService.findUserById(anyLong())).thenReturn(userDto);
        when(itemService.findByItemId(bookingInDto.getItemId())).thenReturn(itemDto);
        Assertions.assertThrows(ValidationException.class,
                () -> bookingService.create(userDto.getId(), bookingInputWrongEndDateDto));
    }

    @Test
    void createBookingWrongStartDate() {
        when(userService.findUserById(anyLong())).thenReturn(userDto);
        when(itemService.findByItemId(bookingInDto.getItemId())).thenReturn(itemDto);
        Assertions.assertThrows(ValidationException.class,
                () -> bookingService.create(userDto.getId(), bookingInputWringStartDateDto));
    }

    @Test
    void createBookingEqualDates() {
        when(userService.findUserById(anyLong())).thenReturn(userDto);
        when(itemService.findByItemId(bookingInDto.getItemId())).thenReturn(itemDto);
        Assertions.assertThrows(ValidationException.class,
                () -> bookingService.create(userDto.getId(), bookingInputEqualDatesDto));
    }

    @Test
    void createBookingEqualOwnerIds() {
        when(userService.findUserById(anyLong())).thenReturn(userDto);
        when(itemService.findByItemId(bookingInDto.getItemId())).thenReturn(itemEqualOwnerIdDto);
        Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.create(userDto.getId(), bookingInDto));
    }

    @Test
    void approvedByOwner() {
        when(bookingRepository.findById(bookingWaiting.getId())).thenReturn(Optional.of(bookingWaiting));
        when(bookingRepository.save(any(Booking.class))).thenReturn(bookingWaiting);

        BookingOutDto approvedBooking = bookingService.approveByOwner(1L, 1L, true);

        Assertions.assertNotNull(approvedBooking);
        Assertions.assertEquals(bookingOutDto.getId(), approvedBooking.getId());
        Assertions.assertEquals(bookingOutDto.getStart(), approvedBooking.getStart());
        Assertions.assertEquals(bookingOutDto.getEnd(), approvedBooking.getEnd());
        Assertions.assertEquals(bookingOutDto.getItem().getId(), approvedBooking.getItem().getId());
        Assertions.assertEquals(bookingOutDto.getBooker().getId(), approvedBooking.getBooker().getId());
        Assertions.assertEquals(bookingOutDto.getStatus(), approvedBooking.getStatus());
    }

    @Test
    void approvedByOwnerStatusApproved() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        Assertions.assertThrows(ValidationException.class,
                () -> bookingService.approveByOwner(1L, 1L, true));
    }

    @Test
    void approvedByOwnerNoItem() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        Assertions.assertThrows(ValidationException.class,
                () -> bookingService.approveByOwner(2L, 1L, true));
    }

    @Test
    void getBookingByBookingIdAndUserId() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        BookingOutDto bookingOutput = bookingService.getBookingByIdAndUser(1L, 1L);

        Assertions.assertNotNull(bookingOutput);
        Assertions.assertEquals(bookingOutDto.getId(), bookingOutput.getId());
        Assertions.assertEquals(bookingOutDto.getStart(), bookingOutput.getStart());
        Assertions.assertEquals(bookingOutDto.getEnd(), bookingOutput.getEnd());
        Assertions.assertEquals(bookingOutDto.getItem().getId(), bookingOutput.getItem().getId());
        Assertions.assertEquals(bookingOutDto.getBooker().getId(), bookingOutput.getBooker().getId());
        Assertions.assertEquals(bookingOutDto.getStatus(), bookingOutput.getStatus());
    }

    @Test
    void getBookingByBookingIdAndUserIdWrongUserId() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.getBookingByIdAndUser(1L, 2L));
    }

    @Test
    void findAllByBooker() {
        when(bookingRepository.getAllBookingsByBookerId(1L, PageRequest.of(0, 1)))
                .thenReturn(List.of(booking));

        List<BookingOutDto> bookings = bookingService.findAllByBooker(1L, State.ALL, 0, 1);

        Assertions.assertEquals(1, bookings.size());
        verify(userService,times(1)).findUserById(1L);
    }

    @Test
    void findAllCurrentByBooker() {
        LocalDateTime currentTime = LocalDateTime.now().withNano(0);

        when(bookingRepository.getAllCurrentBookingsByBookerId(1L, currentTime, PageRequest.of(0, 1)))
                .thenReturn(List.of(booking));

        List<BookingOutDto> bookingsCurrent = bookingService.findAllByBooker(1L, State.CURRENT, 0, 1);

        Assertions.assertEquals(1, bookingsCurrent.size());
        verify(userService,times(1)).findUserById(1L);
    }

    @Test
    void findAllWaitingByBooker() {
        LocalDateTime currentTime = LocalDateTime.now().withNano(0);

        when(bookingRepository.getAllWaitingBookingsByBookerId(1L, currentTime, PageRequest.of(0, 1)))
                .thenReturn(List.of(booking));

        List<BookingOutDto> bookingsCurrent = bookingService.findAllByBooker(1L, State.WAITING, 0, 1);

        Assertions.assertEquals(1, bookingsCurrent.size());
        verify(userService,times(1)).findUserById(1L);
    }

    @Test
    void findAllPastByBooker() {
        LocalDateTime currentTime = LocalDateTime.now().withNano(0);
        System.out.println("currentTime = "+ currentTime);
        System.out.println("booking.start = "+ booking.getStart());
        System.out.println("booking.end = "+ booking.getEnd());

        when(bookingRepository.getAllPastBookingsByBookerId(1L, currentTime, PageRequest.of(0, 1)))
                .thenReturn(List.of(booking));

        List<BookingOutDto> bookingsCurrent = bookingService.findAllByBooker(1L, State.PAST, 0, 1);

        Assertions.assertEquals(1, bookingsCurrent.size());
        verify(userService,times(1)).findUserById(1L);
    }

    @Test
    void findAllFutureByBooker() {
        LocalDateTime currentTime = LocalDateTime.now().withNano(0);
        System.out.println("currentTime = "+ currentTime);
        System.out.println("booking.start = "+ booking.getStart());
        System.out.println("booking.end = "+ booking.getEnd());

        when(bookingRepository.getAllFutureBookingsByBookerId(1L, currentTime, PageRequest.of(0, 1)))
                .thenReturn(List.of(booking));

        List<BookingOutDto> bookingsCurrent = bookingService.findAllByBooker(1L, State.FUTURE, 0, 1);

        Assertions.assertEquals(1, bookingsCurrent.size());
        verify(userService,times(1)).findUserById(1L);
    }

    @Test
    void findAllRejectedByBooker() {
        when(bookingRepository.getAllRejectedBookingsByBookerId(1L, PageRequest.of(0, 1)))
                .thenReturn(List.of(booking));

        List<BookingOutDto> bookingsCurrent = bookingService.findAllByBooker(1L, State.REJECTED, 0, 1);

        Assertions.assertEquals(1, bookingsCurrent.size());
        verify(userService,times(1)).findUserById(1L);
    }

    @Test
    void findAllByBookerWrongState() {
        Assertions.assertThrows(BookingStateException.class,
                () -> bookingService.findAllByBooker(1L, State.UNKNOWN, 0, 1));
    }

    @Test
    void findAllByOwner() {
        when(bookingRepository.getAllBookingsByOwnerId(1L, PageRequest.of(0, 1)))
                .thenReturn(List.of(booking));

        List<BookingOutDto> bookings = bookingService.findAllByOwner(1L, State.ALL, 0, 1);

        Assertions.assertEquals(1, bookings.size());
        verify(userService,times(1)).findUserById(1L);
    }

    @Test
    void findAllCurrentByOwner() {
        LocalDateTime currentTime = LocalDateTime.now().withNano(0);
        System.out.println("currentTime = "+ currentTime);
        System.out.println("booking.start = "+ booking.getStart());
        System.out.println("booking.end = "+ booking.getEnd());

        when(bookingRepository.getAllCurrentBookingsByOwnerId(1L, currentTime, PageRequest.of(0, 1)))
                .thenReturn(List.of(booking));

        List<BookingOutDto> bookingsCurrent = bookingService.findAllByOwner(1L, State.CURRENT, 0, 1);

        Assertions.assertEquals(1, bookingsCurrent.size());
        verify(userService,times(1)).findUserById(1L);
    }

    @Test
    void findAllWaitingByOwner() {
        LocalDateTime currentTime = LocalDateTime.now().withNano(0);

        when(bookingRepository.getAllWaitingBookingsByOwnerId(1L, currentTime, PageRequest.of(0, 1)))
                .thenReturn(List.of(booking));

        List<BookingOutDto> bookingsCurrent = bookingService.findAllByOwner(1L, State.WAITING, 0, 1);

        Assertions.assertEquals(1, bookingsCurrent.size());
        verify(userService,times(1)).findUserById(1L);
    }

    @Test
    void findAllPastByOwner() {
        LocalDateTime currentTime = LocalDateTime.now().withNano(0);

        when(bookingRepository.getAllPastBookingsByOwnerId(1L, currentTime, PageRequest.of(0, 1)))
                .thenReturn(List.of(booking));

        List<BookingOutDto> bookingsCurrent = bookingService.findAllByOwner(1L, State.PAST, 0, 1);

        Assertions.assertEquals(1, bookingsCurrent.size());
        verify(userService,times(1)).findUserById(1L);
    }

    @Test
    void findAllFutureByOwner() {
        LocalDateTime currentTime = LocalDateTime.now().withNano(0);

        when(bookingRepository.getAllFutureBookingsByOwnerId(1L, currentTime, PageRequest.of(0, 1)))
                .thenReturn(List.of(booking));

        List<BookingOutDto> bookingsCurrent = bookingService.findAllByOwner(1L, State.FUTURE, 0, 1);

        Assertions.assertEquals(1, bookingsCurrent.size());
        verify(userService,times(1)).findUserById(1L);
    }

    @Test
    void findAllRejectedByOwner() {
        when(bookingRepository.getAllRejectedBookingsByOwnerId(1L, PageRequest.of(0, 1)))
                .thenReturn(List.of(booking));

        List<BookingOutDto> bookingsCurrent = bookingService.findAllByOwner(1L, State.REJECTED, 0, 1);

        Assertions.assertEquals(1, bookingsCurrent.size());
        verify(userService,times(1)).findUserById(1L);
    }

    @Test
    void findAllByOwnerWrongState() {
        Assertions.assertThrows(BookingStateException.class,
                () -> bookingService.findAllByOwner(1L, State.UNKNOWN, 0, 1));
    }
}