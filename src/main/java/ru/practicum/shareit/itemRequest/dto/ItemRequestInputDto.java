package ru.practicum.shareit.itemRequest.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequestInputDto {

    @NotEmpty
    private String description;
}