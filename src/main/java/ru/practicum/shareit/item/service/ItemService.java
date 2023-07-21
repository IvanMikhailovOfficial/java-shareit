package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto create(ItemDto itemDto, Long userId);

    ItemDto update(ItemDto itemDto, Long itemId, Long userId);

    void delete(Long id);

    ItemDto get(Long itemId, Long userId);

    List<ItemDto> getAll(Long userId);

    List<ItemDto> search(String text, Long userId);
}
