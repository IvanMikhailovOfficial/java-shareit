package ru.practicum.shareit.exceptions.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.exceptions.ErrorResponse;
import ru.practicum.shareit.exceptions.UnsupportedStateException;
import ru.practicum.shareit.user.UserClientController;

@RestControllerAdvice(basePackageClasses = {UserClientController.class, BookingController.class})
public class ErrorHandler {

    @ExceptionHandler(UnsupportedStateException.class)
    public ResponseEntity<?> handleBookingUnsupportedStateException(UnsupportedStateException ex) {
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }
}