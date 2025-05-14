package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookerInfoDto;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.exception.BookingStateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemService itemService;
    private final UserService userService;
    private static final String BOOKING_NOT_FOUND = "Бронирования с id %s нет!";

    @Override
    @Transactional
    public BookingOutDto create(Long userId, BookingInDto bookingInDto) {
        UserDto userDto = userService.findUserById(userId);
        ItemDto itemDto = itemService.findByItemId(bookingInDto.getItemId());

        if (!itemDto.getAvailable()) {
            throw new ValidationException("Вещь недоступна для бронирования!");
        }

        if (bookingInDto.getEnd().isBefore(bookingInDto.getStart())) {
            throw new ValidationException("Дата окончания не может быть раньше даты начала!");
        }

        if (bookingInDto.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Дата начала не может быть раньше текущей даты!");
        }

        if (Objects.equals(itemDto.getOwnerId(), userDto.getId())) {
            throw new NotFoundException("Такой вещи нет!");
        }

        Booking booking = BookingMapper.toBooking(bookingInDto, Status.WAITING, itemDto, userDto);
        return BookingMapper.toBookingCreatedDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingOutDto approveByOwner(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            throw new NotFoundException(String.format(BOOKING_NOT_FOUND, bookingId));
        });

        if (!Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            throw new ValidationException("У пользователя нет такой вещи!");
        }

        if (booking.getStatus().equals(Status.APPROVED)) {
            throw new ValidationException("Статус уже поставлен!");
        }

        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        return BookingMapper.toBookingCreatedDto(bookingRepository.save(booking));
    }

    @Override
    public BookingOutDto getBookingByIdAndUser(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            throw new NotFoundException(String.format(BOOKING_NOT_FOUND, bookingId));
        });

        if (!Objects.equals(booking.getBooker().getId(), userId)
                && !Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            throw new NotFoundException("Такого бронирования нет");
        }

        return BookingMapper.toBookingCreatedDto(booking);
    }

    @Override
    public List<BookingOutDto> findAllByBooker(Long bookerId, State state) {
        userService.findUserById(bookerId);
        LocalDateTime now = LocalDateTime.now();

        return switch (state) {
            case ALL -> BookingMapper
                    .toBookingCreatedDto(bookingRepository.getAllBookingsByBookerId(bookerId));
            case CURRENT -> BookingMapper
                    .toBookingCreatedDto(bookingRepository.getAllCurrentBookingsByBookerId(bookerId, now));
            case PAST -> BookingMapper
                    .toBookingCreatedDto(bookingRepository.getAllPastBookingsByBookerId(bookerId, now));
            case FUTURE -> BookingMapper
                    .toBookingCreatedDto(bookingRepository.getAllFutureBookingsByBookerId(bookerId, now));
            case WAITING -> BookingMapper
                    .toBookingCreatedDto(bookingRepository.getAllWaitingBookingsByBookerId(bookerId, now));
            case REJECTED -> BookingMapper
                    .toBookingCreatedDto(bookingRepository.getAllRejectedBookingsByBookerId(bookerId));
            default -> throw new BookingStateException("Unknown state: " + state);
        };
    }

    @Override
    public List<BookingOutDto> findAllByOwner(Long userId, State state) {
        userService.findUserById(userId);
        LocalDateTime now = LocalDateTime.now();

        return switch (state) {
            case ALL -> BookingMapper
                    .toBookingCreatedDto(bookingRepository.getAllBookingsByOwnerId(userId));
            case CURRENT -> BookingMapper
                    .toBookingCreatedDto(bookingRepository.getAllCurrentBookingsByOwnerId(userId, now));
            case WAITING -> BookingMapper
                    .toBookingCreatedDto(bookingRepository.getAllWaitingBookingsByOwnerId(userId, now));
            case PAST -> BookingMapper
                    .toBookingCreatedDto(bookingRepository.getAllPastBookingsByOwnerId(userId, now));
            case FUTURE -> BookingMapper
                    .toBookingCreatedDto(bookingRepository.getAllFutureBookingsByOwnerId(userId, now));
            case REJECTED -> BookingMapper
                    .toBookingCreatedDto(bookingRepository.getAllRejectedBookingsByOwnerId(userId));
            default -> throw new BookingStateException("Unknown state: " + state);
        };
    }

    @Override
    public void checkBookingsByUserIdAndItemId(long userId, long itemId) {
        List<Booking> bookings = bookingRepository.getAllUserBookings(userId, itemId, LocalDateTime.now());

        if (bookings.isEmpty()) {
            throw new ValidationException("Создай бронирование, чтобы оставить комментарий!");
        }
    }

    public Map<String,BookerInfoDto> getBookingInfo(long itemId) {
        Map<String,BookerInfoDto> map = new HashMap<>();
        map.put("LastBooking",getLastBooking(itemId));
        System.out.println("B-S LastBooking = " + map.get("LastBooking"));
        map.put("NextBooking",getNextBooking(itemId));
        System.out.println("B-S NextBooking = " + map.get("NextBooking"));
        return map;
    }

    private BookerInfoDto getLastBooking(long itemId) {
        return bookingRepository
                .getLastBooking(itemId, LocalDateTime.now())
                .map(BookingMapper::toBookingInfoDto)
                .orElse(null);
    }

    private BookerInfoDto getNextBooking(long itemId) {
        return bookingRepository
                .getNextBooking(itemId, LocalDateTime.now())
                .map(BookingMapper::toBookingInfoDto)
                .orElse(null);
    }
}
