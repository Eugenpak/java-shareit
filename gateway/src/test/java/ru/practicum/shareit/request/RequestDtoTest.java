package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.request.dto.RequestDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class RequestDtoTest {
    @Autowired
    private JacksonTester<RequestDto> json;

    private RequestDto requestDto = null;

    @BeforeEach
    public void setup() {
        requestDto = new RequestDto(
                1L,
                "Хотел фотик",
                LocalDateTime.parse("2025-05-15T14:38:28.100"),
                new ArrayList<>());
    }

    @Test
    public void createdSerializes() throws IOException {
        assertThat(json.write(requestDto))
                .extractingJsonPathStringValue("$.created")
                .isEqualTo("2025-05-15T14:38:28");
    }
}
