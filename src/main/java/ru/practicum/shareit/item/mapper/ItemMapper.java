package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .request(item.getRequest())
                .available(item.getAvailable())
                .id(item.getId())
                .description(item.getDescription())
                .name(item.getName())
                .owner(item.getOwner())
                .build();
    }

    public static Item toItem(ItemDto itemDto) {
        return Item.builder()
                .request(itemDto.getRequest())
                .available(itemDto.getAvailable())
                .id(itemDto.getId())
                .description(itemDto.getDescription())
                .name(itemDto.getName())
                .owner(itemDto.getOwner())
                .build();
    }

    public static List<ItemDto> toItemDtoList(List<Item> itemList) {
        List<ItemDto> list = new ArrayList<>();
        for (Item item : itemList) {
            list.add(toItemDto(item));
        }
        return list;
    }
}
