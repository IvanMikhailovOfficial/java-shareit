package ru.practicum.shareit.itemRequest.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.itemRequest.dto.ItemRequestInputDto;
import ru.practicum.shareit.itemRequest.dto.ItemRequestOutputDto;
import ru.practicum.shareit.itemRequest.model.ItemRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public final class ItemRequestMapper {

    public static ItemRequestOutputDto toInputDto(ItemRequest itemRequest) {
        return ItemRequestOutputDto.builder()
                .id(itemRequest.getId())
                .created(itemRequest.getCreated())
                .description(itemRequest.getDescription())
                .build();
    }

    public static ItemRequest fromInputToItemRequest(Long userId, ItemRequestInputDto itemRequestInputDto) {
        return ItemRequest.builder()
                .requestorId(userId)
                .created(LocalDateTime.now())
                .description(itemRequestInputDto.getDescription())
                .build();
    }

    public static List<ItemRequestOutputDto> toItemRequestOutputList(List<ItemRequest> listItemRequest) {
        List<ItemRequestOutputDto> list = new ArrayList<>();

        for (ItemRequest itemRequest : listItemRequest) {
            list.add(toInputDto(itemRequest));
        }
        return list;
    }

    public static ItemRequestOutputDto toOutputDto(ItemRequest itemRequest) {
        return ItemRequestOutputDto.builder()
                .id(itemRequest.getId())
                .created(itemRequest.getCreated())
                .description(itemRequest.getDescription())
                .build();
    }
}