package ru.practicum.shareit.item.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
@Builder
@Data
public class Item {

    private Long id;
    private String name;
    private String description;
    private Boolean available;
    @JsonIgnore
    private User owner;
    @JsonIgnore
    private ItemRequest request;
}
