package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import ru.practicum.shareit.booking.dto.BookingDtoUser;
import ru.practicum.shareit.comments.dto.CommentResponseDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User owner;
    private Long requestId;
    private BookingDtoUser lastBooking;
    private BookingDtoUser nextBooking;
    private List<CommentResponseDto> comments;
}