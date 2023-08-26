package ru.practicum.shareit.comments.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.comments.model.Comment;

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

    public static List<CommentResponseDto> toListComment(List<Comment> comments) {
        return comments.stream().map(CommentMapperDto::toCommentResponseDto).collect(Collectors.toList());
    }
}