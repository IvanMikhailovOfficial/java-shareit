package ru.practicum.shareit.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingMapperDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comments.dto.CommentResponseDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ItemMapperDto {
    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .owner(item.getOwner())
                .build();
    }

    public static Item fromItemDto(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(itemDto.getOwner())
                .build();
    }

    public static List<ItemDto> toListItemDto(List<Item> items) {
        return items.stream()
                .map(ItemMapperDto::toItemDto)
                .collect(Collectors.toList());
    }

    public static ItemResponseDto toItemResponseDto(Item item,
                                                    Booking lastBooking,
                                                    Booking nextBooking,
                                                    List<CommentResponseDto> comments) {
        return ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .available(item.isAvailable())
                .description(item.getDescription())
                .lastBooking(lastBooking == null ? null : BookingMapperDto.bookingItemResponseDto(lastBooking))
                .nextBooking(nextBooking == null ? null : BookingMapperDto.bookingItemResponseDto(nextBooking))
                .comments(comments)
                .build();
    }
}
