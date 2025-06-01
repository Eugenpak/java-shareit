package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestMapper {
    public static ItemRequest toRequest(ItemRequestDto requestDto, UserDto userDto) {
        ItemRequest request = new ItemRequest();

        request.setId(requestDto.getId());
        request.setDescription(requestDto.getDescription());
        request.setRequestorId(userDto.getId());
        request.setCreated(requestDto.getCreated());

        return request;
    }

    public static ItemRequestDto toRequestDto(ItemRequest request) {
        List<ItemDto> itemDtos = request
                .getItems()
                .stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());

        return new ItemRequestDto(
                request.getId(),
                request.getDescription(),
                request.getCreated(),
                itemDtos);
    }
}

