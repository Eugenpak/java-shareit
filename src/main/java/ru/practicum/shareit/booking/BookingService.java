package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import java.util.List;


public interface BookingService {
    BookingOutDto create(Long userId, BookingInDto bookingInDto);

    BookingOutDto approveByOwner(Long userId, Long bookingId, Boolean approved);

    BookingOutDto getBookingByIdAndUser(Long bookingId, Long userId);

    List<BookingOutDto> findAllByBooker(Long bookerId, State state);

    List<BookingOutDto> findAllByOwner(Long userId, State state);
}
