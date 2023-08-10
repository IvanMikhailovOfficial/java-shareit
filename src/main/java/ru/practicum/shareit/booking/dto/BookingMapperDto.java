package ru.practicum.shareit.booking.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class BookingMapperDto {
    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .end(booking.getEnd())
                .start(booking.getStart())
                .booker(booking.getBooker())
                .item(booking.getItem())
                .status(booking.getStatus())
                .build();
    }

    public static Booking fromBookingDto(BookingDto bookingDto) {
        return Booking.builder()
                .id(bookingDto.getId())
                .end(bookingDto.getEnd())
                .start(bookingDto.getStart())
                .item(bookingDto.getItem())
                .booker(bookingDto.getBooker())
                .status(bookingDto.getStatus())
                .build();
    }

    public static List<BookingDto> toListBookingDto(List<Booking> bookings) {
        return bookings.stream()
                .map(BookingMapperDto::toBookingDto)
                .collect(Collectors.toList());
    }

    public static BookingResponseDto bookingItemResponseDto(Booking booking) {
        return BookingResponseDto.builder()
                .id(booking.getId())
                .status(booking.getStatus())
                .start(booking.getStart())
                .end(booking.getEnd())
                .bookerId(booking.getBooker().getId())
                .build();
    }
}
