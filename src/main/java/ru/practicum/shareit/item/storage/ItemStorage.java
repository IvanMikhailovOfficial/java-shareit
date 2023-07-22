package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    Item create(Item item, Long userId);

    Item update(Item item, Long itemId, Long userId);

    void delete(Long id);

    Item get(Long itemId, Long userId);

    List<Item> getAll(Long userId);

    List<Item> search(String text, Long userId);
}
