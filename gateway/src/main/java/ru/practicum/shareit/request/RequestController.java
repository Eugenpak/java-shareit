package ru.practicum.shareit.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.validation.group.Create;

@RequiredArgsConstructor
@RestController
@RequestMapping("/requests")
@Validated
public class RequestController {
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                         @Validated({Create.class}) @RequestBody RequestDto requestDto) {
        return requestClient.create(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllOwnRequestsById(@RequestHeader("X-Sharer-User-Id") @Positive Long userId) {
        return requestClient.getAllOwnRequestsById(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                                 @PathVariable("requestId") @Positive Long requestId) {
        return requestClient.getByRequestId(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                                 @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
                                                 @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer size) {
        return requestClient.getAllRequests(userId, from, size);
    }
}
