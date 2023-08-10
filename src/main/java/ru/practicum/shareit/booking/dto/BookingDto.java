package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class BookingDto {
    private Long id;
    @NotNull
    private LocalDateTime start;

    @NotNull
    private LocalDateTime end;

    private Item item;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User booker;

    @NotNull
    private BookingStatus status;
}
