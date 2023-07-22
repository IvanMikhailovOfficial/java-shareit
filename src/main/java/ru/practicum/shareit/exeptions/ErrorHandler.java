package ru.practicum.shareit.exeptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.request.controller.RequestController;
import ru.practicum.shareit.user.controller.UserController;

@RestControllerAdvice(basePackageClasses = {BookingController.class, ItemController.class,
        RequestController.class, UserController.class})
public class ErrorHandler {

    @ExceptionHandler(EmailDuplicateException.class)
    public ResponseEntity<?> emailExceptionHandler(EmailDuplicateException e) {
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<?> itemNotFoundHandler(ItemNotFoundException e) {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> userNotFundHandler(UserNotFoundException e) {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ItemNotAvailableException.class)
    public ResponseEntity<?> itemNotAvailableException(ItemNotAvailableException e) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ItemValidatorException.class)
    public ResponseEntity<?> itemValidatorException(ItemValidatorException e) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> responseStatusException(ResponseStatusException e) {
        return new ResponseEntity<>(e.getStatus());
    }
}
