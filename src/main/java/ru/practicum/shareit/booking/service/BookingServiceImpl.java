package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.storage.BookingStorage;

@Service
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingStorage bookingStorage;

    @Autowired
    public BookingServiceImpl(BookingStorage bookingStorage) {
        this.bookingStorage = bookingStorage;
    }
}
