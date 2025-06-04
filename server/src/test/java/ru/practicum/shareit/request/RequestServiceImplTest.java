package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {
    @InjectMocks
    private RequestServiceImpl requestService;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private UserService userService;

    private final UserDto userDto = new UserDto(
            1L,
            "maks@inbox.ru",
            "Maks");

    private final LocalDateTime time = LocalDateTime.now().withNano(0);

    private final ItemRequestDto requestDto = new ItemRequestDto(
            1L,
            "Какой-то запрос",
            time,
            new ArrayList<>());

    private final ItemRequest request = new ItemRequest(
            1L,
            "Какой-то запрос",
            1L,
            time,
            new ArrayList<>());

    @Test
    void createRequest() {
        when(userService.findUserById(anyLong())).thenReturn(userDto);
        when(requestRepository.save(any(ItemRequest.class))).thenReturn(request);

        ItemRequestDto createdRequest = requestService.create(requestDto, userDto.getId());

        Assertions.assertNotNull(createdRequest);
        Assertions.assertEquals(requestDto.getId(), createdRequest.getId());
        Assertions.assertEquals(requestDto.getDescription(), createdRequest.getDescription());
        Assertions.assertEquals(requestDto.getCreated(), createdRequest.getCreated());
        Assertions.assertEquals(requestDto.getItems().size(), createdRequest.getItems().size());

        verify(userService, times(1)).findUserById(anyLong());
        verify(requestRepository, times(1)).save(any(ItemRequest.class));
        verifyNoMoreInteractions(userService,requestRepository);
    }

    @Test
    void getAllOwnRequestsById() {
        when(userService.findUserById(anyLong())).thenReturn(userDto);
        when(requestRepository.findAllByRequestorIdOrderByCreatedDesc(anyLong()))
                .thenReturn(List.of(request));

        List<ItemRequestDto> requests = requestService.getAllRequestsByOwnerId(anyLong());

        Assertions.assertEquals(1, requests.size());
        Assertions.assertEquals(requestDto.getId(), requests.get(0).getId());
        Assertions.assertEquals(requestDto.getDescription(), requests.get(0).getDescription());
        Assertions.assertEquals(requestDto.getCreated(), requests.get(0).getCreated());
        Assertions.assertEquals(requestDto.getItems().size(), requests.get(0).getItems().size());

        verify(userService, times(1)).findUserById(anyLong());
        verify(requestRepository, times(1))
                .findAllByRequestorIdOrderByCreatedDesc(anyLong());
        verifyNoMoreInteractions(userService,requestRepository);
    }

    @Test
    void getByRequestId() {
        when(userService.findUserById(1L)).thenReturn(userDto);
        when(requestRepository.findById(anyLong())).thenReturn(Optional.of(request));

        ItemRequestDto foundRequest = requestService.getByRequestId(anyLong(),1L);

        Assertions.assertNotNull(foundRequest);
        Assertions.assertEquals(requestDto.getId(), foundRequest.getId());
        Assertions.assertEquals(requestDto.getDescription(), foundRequest.getDescription());
        Assertions.assertEquals(requestDto.getCreated(), foundRequest.getCreated());
        Assertions.assertEquals(requestDto.getItems().size(), foundRequest.getItems().size());

        verify(userService, times(1)).findUserById(1L);
        verify(requestRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(userService,requestRepository);
    }

    @Test
    void getByRequestIdNotFound() {
        when(userService.findUserById(1L)).thenReturn(userDto);
        when(requestRepository.findById(anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class,
                () -> requestService.getByRequestId(anyLong(),1L));

        verify(userService, times(1)).findUserById(1L);
        verify(requestRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(userService,requestRepository);
    }

    @Test
    void getAllRequests() {
        Pageable pageable = PageRequest.of(0, 1);
        when(userService.findUserById(1L)).thenReturn(userDto);
        when(requestRepository.findAllByRequestorIdNotOrderByCreatedDesc(1L, pageable))
                .thenReturn(List.of(request));

        List<ItemRequestDto> requests = requestService.getAllRequests(1L, 0, 1);

        Assertions.assertEquals(1, requests.size());
        Assertions.assertEquals(requestDto.getId(), requests.get(0).getId());
        Assertions.assertEquals(requestDto.getDescription(), requests.get(0).getDescription());
        Assertions.assertEquals(requestDto.getCreated(), requests.get(0).getCreated());
        Assertions.assertEquals(requestDto.getItems().size(), requests.get(0).getItems().size());

        verify(userService, times(1)).findUserById(1L);
        verify(requestRepository, times(1))
                .findAllByRequestorIdNotOrderByCreatedDesc(anyLong(), any(Pageable.class));
        verifyNoMoreInteractions(userService,requestRepository);
    }

}