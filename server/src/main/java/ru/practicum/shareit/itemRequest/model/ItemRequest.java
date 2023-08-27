package ru.practicum.shareit.itemRequest.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "item_requests")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "description", nullable = false, length = 512)
    private String description;

    @Column(name = "requestor_id")
    private Long requestorId;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;
}