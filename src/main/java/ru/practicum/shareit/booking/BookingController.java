package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Marker;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingOutDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                @Validated({Marker.OnCreate.class}) @RequestBody BookingInDto bookingInDto) {

        return bookingService.create(userId, bookingInDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingOutDto approveByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @PathVariable("bookingId") Long bookingId,
                                        @RequestParam("approved") Boolean approved) {
        return bookingService.approveByOwner(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingOutDto getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @PathVariable("bookingId") Long bookingId) {
        return bookingService.getBookingByIdAndUser(bookingId, userId);
    }

    @GetMapping
    public List<BookingOutDto> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestParam(value = "state", defaultValue = "ALL") State state) {

        return bookingService.findAllByBooker(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingOutDto> getAllByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestParam(value = "state", defaultValue = "ALL") State state) {

        return bookingService.findAllByOwner(userId, state);
    }
}
