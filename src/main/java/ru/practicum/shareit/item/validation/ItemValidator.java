package ru.practicum.shareit.item.validation;

import ru.practicum.shareit.exeptions.ItemValidatorException;
import ru.practicum.shareit.item.dto.ItemDto;

public class ItemValidator {
    public static void itemValidate(ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new ItemValidatorException("Имя не может быть пустым");
        }

        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new ItemValidatorException("Описание не может быть пустым");
        }
    }
}
