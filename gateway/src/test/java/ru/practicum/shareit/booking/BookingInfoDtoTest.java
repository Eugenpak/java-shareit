package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.BookerInfoDto;

import java.io.IOException;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingInfoDtoTest {
    @Autowired
    private JacksonTester<BookerInfoDto> json;

    private static final String DATE_TIME = "2023-03-15T14:38:28";

    private BookerInfoDto bookerInfoDto = null;

    @BeforeEach
    public void setup() {
        bookerInfoDto = new BookerInfoDto(
                4L,
                5L,
                LocalDateTime.parse("2023-03-15T14:38:28.100"),
                LocalDateTime.parse("2023-03-15T14:38:28.100")
        );
    }

    @Test
    public void startSerializes() throws IOException {
        assertThat(json.write(bookerInfoDto))
                .extractingJsonPathStringValue("$.start")
                .isEqualTo(DATE_TIME);
    }

    @Test
    public void endSerializes() throws IOException {
        assertThat(json.write(bookerInfoDto))
                .extractingJsonPathStringValue("$.end")
                .isEqualTo(DATE_TIME);
    }
}
