package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestDtoTest {
    @Autowired
    private JacksonTester<ItemRequestDto> json;

    private ItemRequestDto requestDto = null;

    @BeforeEach
    public void setup() {
        requestDto = new ItemRequestDto(
                1L,
                "Хотел бы воспользоваться щёткой для обуви",
                LocalDateTime.parse("2023-03-15T14:38:28.123"),
                new ArrayList<>());
    }

    @Test
    public void createdSerializes() throws IOException {
        assertThat(json.write(requestDto))
                .extractingJsonPathStringValue("$.created")
                .isEqualTo("2023-03-15T14:38:28");
    }
}
