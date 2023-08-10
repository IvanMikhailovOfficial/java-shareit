package ru.practicum.shareit.exceptions.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.exceptions.exp.EmailDuplicateException;
import ru.practicum.shareit.exceptions.exp.EntityNotFoundException;
import ru.practicum.shareit.exceptions.exp.NotAvailableException;
import ru.practicum.shareit.exceptions.exp.ValidateException;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.user.UserController;

import java.util.Map;

@RestControllerAdvice(basePackageClasses = {UserController.class, ItemController.class, BookingController.class})
public class ErrorHandler {


    @ExceptionHandler(EmailDuplicateException.class)
    public ResponseEntity<ErrorResponse> emailDublExcHandler(final EmailDuplicateException e) {
        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> responseStatusExcHandler(final ResponseStatusException e) {
        return new ResponseEntity<>(new ErrorResponse(e.getReason()), e.getStatus());
    }


    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> notFoundExcHandler(final EntityNotFoundException e) {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NotAvailableException.class)
    public ResponseEntity<ErrorResponse> notAvailableExcHandler(final NotAvailableException e) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ValidateException.class)
    public ResponseEntity<ErrorResponse> validateExcHandler(final ValidateException e) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }


}
