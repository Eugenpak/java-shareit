package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    @MockBean
    private UserService userService;

    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mvc;

    private final UserDto userDto = new UserDto(
            1L,
            "Igor",
            "igor@gmail.dom");

    private final ItemDto itemDto = new ItemDto(
            1L,
            "Какая-то вещь",
            "Какое-то описание",
            true,
            1L,
            null,
            null,
            new ArrayList<>(),
            1L);

    private final BookingOutDto bookingOutDto = new BookingOutDto(
            1L,
            LocalDateTime.now().withNano(0),
            LocalDateTime.now().withNano(0).plusDays(1),
            Status.WAITING,
            userDto,
            itemDto);

    private final BookingInDto bookingInDto = new BookingInDto(
            1L,
            LocalDateTime.now().withNano(0),
            LocalDateTime.now().withNano(0).plusDays(1));

    @BeforeEach
    void setup() {
        //when(userService.findUserById(anyLong())).thenReturn(userDto);
    }

    @Test
    void createNewBooking() throws Exception {
        when(bookingService.create(any(Long.class),any(BookingInDto.class)))
                .thenReturn(bookingOutDto);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingInDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingOutDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingOutDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingOutDto.getEnd().toString())))
                .andExpect(jsonPath("$.status", is(bookingOutDto.getStatus().name())))
                .andExpect(jsonPath("$.booker.id", is(bookingOutDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingOutDto.getItem().getId()), Long.class));
    }

    @Test
    void approvedByOwner() throws Exception {
        when(bookingService.approveByOwner(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingOutDto);

        mvc.perform(patch("/bookings/{bookingId}", "1")
                        .content(mapper.writeValueAsString(bookingInDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingOutDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingOutDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingOutDto.getEnd().toString())))
                .andExpect(jsonPath("$.status", is(bookingOutDto.getStatus().name())))
                .andExpect(jsonPath("$.booker.id", is(bookingOutDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingOutDto.getItem().getId()), Long.class));
    }

    @Test
    void getById() throws Exception {
        when(bookingService.getBookingByIdAndUser(anyLong(), anyLong())).thenReturn(bookingOutDto);

        mvc.perform(get("/bookings/{bookingId}", "1")
                        .content(mapper.writeValueAsString(bookingInDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingOutDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingOutDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingOutDto.getEnd().toString())))
                .andExpect(jsonPath("$.status", is(bookingOutDto.getStatus().name())))
                .andExpect(jsonPath("$.booker.id", is(bookingOutDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingOutDto.getItem().getId()), Long.class));
    }

    @Test
    void getAll() throws Exception {

        when(bookingService.findAllByBooker(anyLong(), any(State.class), anyInt(), anyInt()))
                .thenReturn(List.of(bookingOutDto));

        mvc.perform(get("/bookings")
                        .content(mapper.writeValueAsString(bookingInDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingOutDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingOutDto.getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(bookingOutDto.getEnd().toString())))
                .andExpect(jsonPath("$[0].status", is(bookingOutDto.getStatus().name())))
                .andExpect(jsonPath("$[0].booker.id", is(bookingOutDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(bookingOutDto.getItem().getId()), Long.class));
    }

    @Test
    void getAllByOwner() throws Exception {
        when(bookingService.findAllByOwner(anyLong(), any(State.class), anyInt(), anyInt()))
                .thenReturn(List.of(bookingOutDto));

        mvc.perform(get("/bookings/owner")
                        .content(mapper.writeValueAsString(bookingInDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingOutDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingOutDto.getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(bookingOutDto.getEnd().toString())))
                .andExpect(jsonPath("$[0].status", is(bookingOutDto.getStatus().name())))
                .andExpect(jsonPath("$[0].booker.id", is(bookingOutDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(bookingOutDto.getItem().getId()), Long.class));
    }


}