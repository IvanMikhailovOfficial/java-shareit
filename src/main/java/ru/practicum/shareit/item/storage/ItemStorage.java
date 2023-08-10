package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {
    Optional<Item> save(Long userId, ItemDto itemDto);

    Optional<Item> patch(Long id, ItemDto itemDto);

    Optional<Item> findById(Long id);

    void delete(Long id);

    List<Item> findAllItemByUserId(Long userId);

    List<Item> findByText(String text);
}
