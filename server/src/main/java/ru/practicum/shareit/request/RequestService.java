package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface RequestService {
    ItemRequestDto create(ItemRequestDto requestDto, Long userId);

    List<ItemRequestDto> getAllRequestsByOwnerId(Long userId);

    ItemRequestDto getByRequestId(Long requestId,Long userId);

    List<ItemRequestDto> getAllRequests(Long userId,Integer from,Integer size);
}
