package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.exception.BookingStateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
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
            log.warn("B-S create(). BAD_REQUEST. item-> unavailable!");
            throw new ValidationException("Вещь недоступна для бронирования!");
        }

        if (bookingInDto.getEnd().isBefore(bookingInDto.getStart())) {
            log.warn("B-S create(). BAD_REQUEST. bookingInDto-> End-Start!");
            throw new ValidationException("Дата окончания не может быть раньше даты начала!");
        }

        if (bookingInDto.getStart().isBefore(LocalDateTime.now())) {
            log.warn("B-S create(). BAD_REQUEST. bookingInDto-> Start-Now!");
            throw new ValidationException("Дата начала не может быть раньше текущей даты!");
        }

        if (bookingInDto.getStart().isEqual(bookingInDto.getEnd())) {
            log.warn("B-S create(). BAD_REQUEST. bookingInDto-> Start != End!");
            throw new ValidationException("Дата начала не может быть равна дате окончания");
        }

        if (Objects.equals(itemDto.getOwnerId(), userDto.getId())) {
            log.warn("B-S create(). NOT_FOUND. itemDto-> owner!");
            throw new NotFoundException("Такой вещи нет!");
        }

        Booking booking = BookingMapper.toBooking(bookingInDto, Status.WAITING, itemDto, userDto);
        return BookingMapper.toBookingCreatedDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingOutDto approveByOwner(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            log.warn("B-S approveByOwner(). NOT_FOUND. booking-> notId!");
            throw new NotFoundException(String.format(BOOKING_NOT_FOUND, bookingId));
        });

        if (!Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            log.warn("B-S approveByOwner(). BAD_REQUEST. booking-> notOwner!");
            throw new ValidationException("У пользователя нет такой вещи!");
        }

        if (booking.getStatus().equals(Status.APPROVED)) {
            log.warn("B-S approveByOwner(). BAD_REQUEST. booking-> status==Approved!");
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
    public List<BookingOutDto> findAllByBooker(Long bookerId, State state,Integer from,Integer size) {
        userService.findUserById(bookerId);
        LocalDateTime now = LocalDateTime.now().withNano(0);
        Pageable pageable = PageRequest.of(from / size, size);

        return switch (state) {
            case ALL -> BookingMapper
                    .toBookingCreatedDto(bookingRepository.getAllBookingsByBookerId(bookerId, pageable));
            case CURRENT -> BookingMapper
                    .toBookingCreatedDto(bookingRepository.getAllCurrentBookingsByBookerId(bookerId, now, pageable));
            case PAST -> BookingMapper
                    .toBookingCreatedDto(bookingRepository.getAllPastBookingsByBookerId(bookerId, now, pageable));
            case FUTURE -> BookingMapper
                    .toBookingCreatedDto(bookingRepository.getAllFutureBookingsByBookerId(bookerId, now, pageable));
            case WAITING -> BookingMapper
                    .toBookingCreatedDto(bookingRepository.getAllWaitingBookingsByBookerId(bookerId, now, pageable));
            case REJECTED -> BookingMapper
                    .toBookingCreatedDto(bookingRepository.getAllRejectedBookingsByBookerId(bookerId, pageable));
            default -> throw new BookingStateException("Unknown state: " + state);
        };
    }

    @Override
    public List<BookingOutDto> findAllByOwner(Long userId, State state,Integer from,Integer size) {
        userService.findUserById(userId);
        LocalDateTime now = LocalDateTime.now().withNano(0);
        Pageable pageable = PageRequest.of(from / size, size);

        return switch (state) {
            case ALL -> BookingMapper
                    .toBookingCreatedDto(bookingRepository.getAllBookingsByOwnerId(userId,pageable));
            case CURRENT -> BookingMapper
                    .toBookingCreatedDto(bookingRepository.getAllCurrentBookingsByOwnerId(userId, now,pageable));
            case WAITING -> BookingMapper
                    .toBookingCreatedDto(bookingRepository.getAllWaitingBookingsByOwnerId(userId, now,pageable));
            case PAST -> BookingMapper
                    .toBookingCreatedDto(bookingRepository.getAllPastBookingsByOwnerId(userId, now,pageable));
            case FUTURE -> BookingMapper
                    .toBookingCreatedDto(bookingRepository.getAllFutureBookingsByOwnerId(userId, now,pageable));
            case REJECTED -> BookingMapper
                    .toBookingCreatedDto(bookingRepository.getAllRejectedBookingsByOwnerId(userId,pageable));
            default -> throw new BookingStateException("Unknown state: " + state);
        };
    }
}
