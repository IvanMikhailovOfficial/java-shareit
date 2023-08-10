package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comments.dto.CommentRequestDto;
import ru.practicum.shareit.comments.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.util.List;

public interface ItemService {
    ItemDto save(Long userId, ItemDto itemDto);

    ItemDto patch(Long itemId, Long userId, ItemDto itemDto);

    ItemResponseDto findById(Long itemId, Long userId);

    void delete(Long id);

    List<ItemResponseDto> findAllItemByUserId(Long userId);

    List<ItemDto> findByText(String text);

    CommentResponseDto addComment(Long userId, Long itemId, CommentRequestDto commentRequestDto);
}
