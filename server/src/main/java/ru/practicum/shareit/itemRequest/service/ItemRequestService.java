package ru.practicum.shareit.itemRequest.service;

import ru.practicum.shareit.itemRequest.dto.ItemRequestInputDto;
import ru.practicum.shareit.itemRequest.dto.ItemRequestOutputDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestOutputDto createItemRequest(Long userId, ItemRequestInputDto itemRequestInputDto);

    ItemRequestOutputDto getItemRequestsByIdOwner(Long userId, Long itemRequestId);

    List<ItemRequestOutputDto> getItemRequestsByOtherUsers(Long userId, Integer from, Integer size);

    List<ItemRequestOutputDto> getItemRequest(Long userId);
}