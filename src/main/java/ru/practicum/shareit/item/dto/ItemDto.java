package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import ru.practicum.shareit.booking.dto.BookingDtoUser;
import ru.practicum.shareit.comments.dto.CommentResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ItemDto {
    private Long id;
    @NotBlank
    private String name;
    @NotNull
    private String description;
    @NotNull
    private Boolean available;
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User owner;
    private ItemRequest request;
    private BookingDtoUser lastBooking;
    private BookingDtoUser nextBooking;
    private List<CommentResponseDto> comments;
}
