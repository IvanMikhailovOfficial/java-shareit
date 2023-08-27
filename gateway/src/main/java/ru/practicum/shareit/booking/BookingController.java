package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exceptions.UnsupportedStateException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;
    private final String xSharer = "X-Sharer-User-Id";

    @GetMapping
    public ResponseEntity<Object> getUserBookings(@RequestHeader(value = xSharer) Long userId,
                                                  @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
                                                  @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                  @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new UnsupportedStateException("Unknown state: " + stateParam));
        log.info("Получен GET запрос getUserBookings  по эндпоинту /bookings/ со значениями  userID {}", userId);
        return bookingClient.getUserBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByOwner(@RequestHeader(value = xSharer) Long userId,
                                                     @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
                                                     @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                                     Integer from,
                                                     @Positive @RequestParam(name = "size", defaultValue = "10")
                                                     Integer size) {
        BookingState state = BookingState.from(stateParam.toUpperCase())
                .orElseThrow(() -> new UnsupportedStateException("Unknown state: " + stateParam));
        log.info("Получен GET запрос getBookingsByOwner  по эндпоинту /bookings/owner со значениями  userID {}", userId);
        return bookingClient.getBookingsByOwner(userId, state, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader(value = xSharer) Long userId,
                                                @RequestBody @Valid BookItemRequestDto requestDto) {
        if (requestDto.getEnd() == null || requestDto.getStart() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        log.info("Получен POST  запрос createBooking по эндпоинту /bookings/ со значениями userId{}", userId);
        return bookingClient.createBooking(userId, requestDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> readBookingById(@RequestHeader(value = xSharer) Long userId,
                                                  @PathVariable Long bookingId) {
        log.info("Получен GET запрос по эндпоинту /bookings/{bookingId} со значениями userId{} и bookingId {}", userId,
                bookingId);
        return bookingClient.readBookingById(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBooking(@RequestHeader(value = xSharer) Long userId,
                                                @PathVariable Long bookingId,
                                                @RequestParam(name = "approved",
                                                        required = false) Boolean approved) {
        log.info("Получен PATCH запрос updateBooking  по эндпоинту /bookings/{bookingId} со значениями userId{} и " +
                "bookingId {}", userId, bookingId);
        return bookingClient.updateBooking(bookingId, userId, approved);
    }
}