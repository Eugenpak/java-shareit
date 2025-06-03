package ru.practicum.shareit.request;



import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final RequestService requestService;

    @PostMapping
    public ItemRequestDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                             @RequestBody ItemRequestDto requestDto) {
        return requestService.create(requestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getAllRequestsByOwnerId(@RequestHeader("X-Sharer-User-Id") Long userId) {

        return requestService.getAllRequestsByOwnerId(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @PathVariable("requestId") Long requestId) {

        return requestService.getByRequestId(requestId,userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
                                               @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {

        return requestService.getAllRequests(userId, from, size);
    }
}
