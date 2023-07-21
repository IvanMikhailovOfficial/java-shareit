package ru.practicum.shareit.request.mapper;


import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

public class RequestMapper {

    public static ItemRequestDto toRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .created(itemRequest.getCreated())
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requestor(itemRequest.getRequestor())
                .build();
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        return ItemRequest.builder()
                .created(itemRequestDto.getCreated())
                .id(itemRequestDto.getId())
                .description(itemRequestDto.getDescription())
                .requestor(itemRequestDto.getRequestor())
                .build();
    }
}
