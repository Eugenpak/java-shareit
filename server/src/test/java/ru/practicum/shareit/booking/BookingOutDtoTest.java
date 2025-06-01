package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingOutDto;

import java.io.IOException;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingOutDtoTest {
    @Autowired
    private JacksonTester<BookingOutDto> json;

    private static final String DATE_TIME = "2025-05-15T14:38:28";

    private BookingOutDto bookingOutputDto = null;

    @BeforeEach
    public void setup() {
        bookingOutputDto = new BookingOutDto(
                2L,
                LocalDateTime.parse("2025-05-15T14:38:28.100"),
                LocalDateTime.parse("2025-05-15T14:38:28.100"),
                Status.WAITING,
                null,
                null);
    }

    @Test
    public void startSerializes() throws IOException {
        assertThat(json.write(bookingOutputDto))
                .extractingJsonPathStringValue("$.start")
                .isEqualTo(DATE_TIME);
    }

    @Test
    public void endSerializes() throws IOException {
        assertThat(json.write(bookingOutputDto))
                .extractingJsonPathStringValue("$.end")
                .isEqualTo(DATE_TIME);
    }
}
