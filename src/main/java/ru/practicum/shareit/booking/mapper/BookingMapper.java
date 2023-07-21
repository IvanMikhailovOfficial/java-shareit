package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

public class BookingMapper {

    public static Booking toBooking(BookingDto bookingDto) {
        return Booking.builder()
                .item(bookingDto.getItem())
                .booker(bookingDto.getBooker())
                .end(bookingDto.getEnd())
                .id(bookingDto.getId())
                .start(bookingDto.getStart())
                .status(bookingDto.getStatus())
                .build();
    }

    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .item(booking.getItem())
                .end(booking.getEnd())
                .id(booking.getId())
                .start(booking.getStart())
                .status(booking.getStatus())
                .booker(booking.getBooker())
                .build();
    }
}
