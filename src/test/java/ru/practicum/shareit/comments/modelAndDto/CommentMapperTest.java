package ru.practicum.shareit.comments.modelAndDto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.comments.dto.CommentMapperDto;
import ru.practicum.shareit.comments.dto.CommentResponseDto;
import ru.practicum.shareit.comments.model.Comment;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public class CommentMapperTest {
    private final Comment comment = Comment.builder()
            .id(1L)
            .author(new User(1L, "123", "123@mail.ru"))
            .created(LocalDateTime.MAX)
            .build();
    private final CommentResponseDto commentResponseDto = CommentResponseDto.builder()
            .id(1L)
            .created(LocalDateTime.MAX)
            .authorName("123")
            .build();

    @Test
    void toCommentResponseDtoTest() {

        Assertions.assertEquals(commentResponseDto, CommentMapperDto.toCommentResponseDto(comment));
    }

    @Test
    void toListCommentTest() {
        List<Comment> comments = List.of(comment);
        List<CommentResponseDto> responseDtos = List.of(commentResponseDto);
        Assertions.assertArrayEquals(responseDtos.toArray(), CommentMapperDto.toListComment(comments).toArray());
    }
}