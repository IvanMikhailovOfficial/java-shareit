package ru.practicum.shareit.itemRequest.dto;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequestOutputDto {
    private Long id;
    private String description;
    private LocalDateTime created;
    @Builder.Default
    private List<ItemDto> items = new ArrayList<>();
}