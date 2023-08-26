package ru.practicum.shareit.itemRequest.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequestInputDto {
    private String description;
}