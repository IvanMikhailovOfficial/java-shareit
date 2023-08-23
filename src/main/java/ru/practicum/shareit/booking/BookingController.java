package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    private final static String X_SHERER = "X-Sharer-User-Id";

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> readBookingById(@RequestHeader(X_SHERER) Long userId,
                                                      @PathVariable Long bookingId) {
        log.info("Получен GET запрос по эндпоинту /bookings/{bookingId} со значениями userId{} и bookingId {}", userId,
                bookingId);
        return new ResponseEntity<>(bookingService.getBookingByIdAndBooker(userId, bookingId), HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<BookingDto> createBooking(@RequestHeader(X_SHERER) Long userId,
                                                    @RequestBody @Valid BookingCreateDto bookingCreateDto,
                                                    BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        log.info("Получен POST  запрос createBooking по эндпоинту /bookings/ со значениями userId{}", userId);
        return new ResponseEntity<>(bookingService.create(userId, bookingCreateDto), HttpStatus.CREATED);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> updateBooking(@PathVariable Long bookingId,
                                                    @RequestHeader(X_SHERER) Long userId,
                                                    @RequestParam(name = "approved", required = false) Boolean approved) {
        log.info("Получен PATCH запрос updateBooking  по эндпоинту /bookings/{bookingId} со значениями userId{} и " +
                "bookingId {}", userId, bookingId);
        return new ResponseEntity<>(bookingService.update(bookingId, userId, approved), HttpStatus.OK);
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<String> deleteBookingById(@PathVariable Long bookingId) {
        bookingService.deleteBookingById(bookingId);

        log.info("Получен DELETE запрос deleteBookingById  по эндпоинту /bookings/{bookingId} со значениями bookingId {}",
                bookingId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<List<BookingDto>> getUserBookings(@RequestHeader(X_SHERER) Long userId,
                                                            @RequestParam(name = "state",
                                                                    defaultValue = "ALL") String state,
                                                            @RequestParam(name = "size", required = false) Integer size,
                                                            @RequestParam(name = "from", required = false) Integer from) {
        log.info("Получен GET запрос getUserBookings  по эндпоинту /bookings/ со значениями  userID {}", userId);
        return new ResponseEntity<>(bookingService.getUserBookings(userId, state, from, size), HttpStatus.OK);
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDto>> getBookingsByOwner(@RequestHeader(X_SHERER) Long userId,
                                                               @RequestParam(name = "state",
                                                                       defaultValue = "ALL") String state,
                                                               @RequestParam(name = "size", required = false) Integer size,
                                                               @RequestParam(name = "from", required = false) Integer from) {
        log.info("Получен GET запрос getBookingsByOwner  по эндпоинту /bookings/owner со значениями  userID {}", userId);
        return new ResponseEntity<>(bookingService.getOwnerBookings(userId, state, from, size), HttpStatus.OK);
    }
}