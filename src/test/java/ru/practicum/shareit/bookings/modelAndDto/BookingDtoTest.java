package ru.practicum.shareit.bookings.modelAndDto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapperDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoTest {
    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    @SneakyThrows
    void testSerialize() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);
        BookingDto bookingDto = BookingDto.builder()
                .id(1L)
                .start(start)
                .end(end)
                .status(BookingStatus.WAITING)
                .item(Item.builder()
                        .owner(User.builder()
                                .id(1L)
                                .name("name")
                                .email("email@mail.ru")
                                .build())
                        .id(1L)
                        .name("name")
                        .description("description")
                        .available(true)
                        .requestId(1L)
                        .build())
                .booker(User.builder()
                        .id(1L)
                        .name("name")
                        .email("email@mail.ru")
                        .build())
                .build();

        JsonContent<BookingDto> result = json.write(bookingDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.description").isEqualTo("description");
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("name");
        assertThat(result).extractingJsonPathBooleanValue("$.item.available").isTrue();
        assertThat(result).extractingJsonPathNumberValue("$.item.owner.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.owner.name").isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("$.item.owner.email").isEqualTo("email@mail.ru");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("$.booker.email").isEqualTo("email@mail.ru");
    }

    @Test
    @SneakyThrows
    void testDeserialize() {
        String jsonContent = "{\"id\":1,\"start\":\"2023-07-04T00:40:59\"," +
                "\"end\":\"2023-07-05T00:40:59\"," +
                "\"status\":\"WAITING\",\"item\":{\"id\":1,\"name\":\"name\",\"description\":\"description\"," +
                "\"available\":true,\"requestId\":1}," +
                "\"booker\":{\"id\":1,\"name\":\"name\",\"email\":\"email@mail.ru\"}}";

        BookingDto bookingDto = json.parse(jsonContent).getObject();

        assertThat(bookingDto).isNotNull();
        assertThat(bookingDto.getId()).isEqualTo(1);
        assertThat(bookingDto.getStart()).isEqualTo(LocalDateTime.parse("2023-07-04T00:40:59"));
        assertThat(bookingDto.getEnd()).isEqualTo(LocalDateTime.parse("2023-07-05T00:40:59"));
        assertThat(bookingDto.getStatus()).isEqualTo(BookingStatus.WAITING);
        assertThat(bookingDto.getItem()).isNotNull();
        assertThat(bookingDto.getItem().getId()).isEqualTo(1);
        assertThat(bookingDto.getItem().getName()).isEqualTo("name");
        assertThat(bookingDto.getItem().getDescription()).isEqualTo("description");
        assertThat(bookingDto.getItem().isAvailable()).isTrue();
        assertThat(bookingDto.getItem().getRequestId()).isEqualTo(1);
        assertThat(bookingDto.getBooker()).isNotNull();
        assertThat(bookingDto.getBooker().getId()).isEqualTo(1);
        assertThat(bookingDto.getBooker().getName()).isEqualTo("name");
        assertThat(bookingDto.getBooker().getEmail()).isEqualTo("email@mail.ru");
    }

    @Test
    void bookingToItemResponseDtoTest() {
        Booking booking = Booking.builder()
                .id(1L)
                .end(LocalDateTime.MAX)
                .start(LocalDateTime.MIN)
                .booker(new User(3L, "bbb", "123@mail.ru"))
                .build();

        BookingResponseDto expected = BookingResponseDto.builder()
                .id(1L)
                .end(LocalDateTime.MAX)
                .start(LocalDateTime.MIN)
                .bookerId(3L)
                .build();
        Assertions.assertEquals(expected, BookingMapperDto.bookingItemResponseDto(booking));
    }
}