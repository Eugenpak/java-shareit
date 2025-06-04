package ru.practicum.shareit.booking;

import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingState;


@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;

	@GetMapping
	public ResponseEntity<Object> getBookings(@RequestHeader("X-Sharer-User-Id") @Positive long userId,
			@RequestParam(name = "state", defaultValue = "all") String stateParam,
			@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") @Min(0) Integer from,
			@Positive @RequestParam(name = "size", defaultValue = "10") @Min(1) Integer size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
		return bookingClient.getBookings(userId, state, from, size);
	}

	@PostMapping
	public ResponseEntity<Object> bookItem(@RequestHeader("X-Sharer-User-Id") @Positive long userId,
			@RequestBody @Validated BookingInputDto requestDto) {  //@RequestBody @Valid BookItemRequestDto requestDto
		log.info("Creating booking {}, userId={}", requestDto, userId);
		return bookingClient.bookItem(userId, requestDto);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") @Positive long userId,
			@PathVariable Long bookingId) {
		log.info("Get booking {}, userId={}", bookingId, userId);
		return bookingClient.getBooking(userId, bookingId);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> approveByOwner(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
												 @RequestParam("approved") Boolean approved,
												 @PathVariable("bookingId") @Positive Long bookingId) {
		return bookingClient.approveByOwner(userId, bookingId, approved);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getAllByOwner(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
												@RequestParam(value = "state", defaultValue = "ALL") BookingState state,
												@RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
												@RequestParam(value = "size", defaultValue = "10") @Min(1) Integer size) {
		return bookingClient.getAllByOwner(userId, state, from, size);
	}

}
