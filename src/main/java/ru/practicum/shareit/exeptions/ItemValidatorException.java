package ru.practicum.shareit.exeptions;

public class ItemValidatorException extends RuntimeException{
    public ItemValidatorException(String message) {
        super(message);
    }
}
