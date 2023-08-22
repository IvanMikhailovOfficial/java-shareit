package ru.practicum.shareit.comments.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.comments.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class CommentMapperDto {
    public static CommentResponseDto toCommentResponseDto(Comment comment) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .created(comment.getCreated())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .build();
    }

    public static Comment fromCommentRequestDto(CommentRequestDto commentRequestDto, Item item, User user,
                                                LocalDateTime now) {
        return Comment.builder()
                .text(commentRequestDto.getText())
                .item(item)
                .author(user)
                .created(now)
                .build();
    }

    public static List<CommentResponseDto> toListComment(List<Comment> comments) {
        return comments.stream().map(CommentMapperDto::toCommentResponseDto).collect(Collectors.toList());
    }
}
