package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserService userService;

    @Override
    public ItemRequestDto create(ItemRequestDto requestDto, Long userId) {
        UserDto userDto = userService.findUserById(userId);
        ItemRequest request = RequestMapper.toRequest(requestDto, userDto);

        return RequestMapper.toRequestDto(requestRepository.save(request));
    }

    @Override
    public List<ItemRequestDto> getAllRequestsByOwnerId(Long userId) {
        UserDto userDto = userService.findUserById(userId);
        List<ItemRequest> requests = requestRepository.findAllByRequestorIdOrderByCreatedDesc(userId);

        return requests
                .stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getByRequestId(Long requestId,Long userId) {
        userService.findUserById(userId);
        ItemRequest request = requestRepository.findById(requestId).orElseThrow(() -> {
            throw new NotFoundException(String.format("Запроса с id %s нет", requestId));
        });

        return RequestMapper.toRequestDto(request);
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId,Integer from,Integer size) {
        userService.findUserById(userId);
        List<ItemRequest> requests = requestRepository
                .findAllByRequestorIdNotOrderByCreatedDesc(userId, PageRequest.of(from, size));

        return requests
                .stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }
}
