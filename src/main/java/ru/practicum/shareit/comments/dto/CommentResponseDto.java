package ru.practicum.shareit.comments.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CommentResponseDto {
    private Long id;
    private String text;
    private String authorName;
    private LocalDateTime created;
}
