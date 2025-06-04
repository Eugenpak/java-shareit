package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class) //RestExceptionHandler
class ItemRequestControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private RequestService requestService;

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mvc;

    private final UserDto userDto = new UserDto(
            1L,
            "marija@mail.ru",
            "Marija");

    private final ItemRequestDto requestDto = new ItemRequestDto(
            1L,
            "Какой-то запрос",
            LocalDateTime.now().withNano(0),
            new ArrayList<>());

    @BeforeEach
    void setup() {
        //when(userService.findById(anyLong())).thenReturn(userDto);
    }

    @Test
    void createNewRequest() throws Exception {
        when(requestService.create(any(), any())).thenReturn(requestDto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is(requestDto.getDescription())));
    }

    @Test
    void getAllRequestsByOwnerId() throws Exception {
        when(requestService.getAllRequestsByOwnerId(anyLong())).thenReturn(List.of(requestDto));

        mvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(requestDto.getDescription())))
                .andExpect(jsonPath("$[0].created", is(requestDto.getCreated().withNano(0)
                        .toString())))
                .andExpect(jsonPath("$[0].items.size()", is(0)));
    }

    @Test
    void getRequestById() throws Exception {
        when(requestService.getByRequestId(anyLong(),anyLong())).thenReturn(requestDto);

        mvc.perform(get("/requests/{requestId}", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDto.getDescription())))
                .andExpect(jsonPath("$.created", is(requestDto.getCreated()
                        .withNano(0).toString())))
                .andExpect(jsonPath("$.items.size()", is(0)));
    }

    @Test
    void getAllRequests() throws Exception {
        when(requestService.getAllRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(requestDto));

        mvc.perform(get("/requests/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .param("from", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(requestDto.getDescription())))
                .andExpect(jsonPath("$[0].created", is(requestDto.getCreated()
                        .withNano(0).toString())))
                .andExpect(jsonPath("$[0].items.size()", is(0)));
    }

    @Test
    void getAllRequestsByWrongParam() throws Exception {
        /*
        when(requestService.getAllRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(requestDto));

        mvc.perform(get("/requests/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .param("from", "-1")
                        .param("size", "-1"))
                        .andExpect(status().isInternalServerError());   */
    }
}