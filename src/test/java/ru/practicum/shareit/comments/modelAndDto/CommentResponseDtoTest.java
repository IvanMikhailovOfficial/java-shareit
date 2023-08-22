package ru.practicum.shareit.comments.modelAndDto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.comments.dto.CommentResponseDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentResponseDtoTest {
    @Autowired
    private JacksonTester<CommentResponseDto> json;

    @Test
    @SneakyThrows
    void testSerialize() {
        LocalDateTime created = LocalDateTime.now();
        CommentResponseDto commentResponseDto = CommentResponseDto.builder()
                .id(1L)
                .text("text")
                .authorName("name")
                .created(created)
                .build();

        JsonContent<CommentResponseDto> result = json.write(commentResponseDto);
        System.out.println(result);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("text");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("name");
    }

    @Test
    @SneakyThrows
    void testDeserialize() {
        String jsonContent = "{\"id\":1,\"text\":\"text\",\"authorName\":\"name\",\"created\":\"2023-07-04T03:38:40\"}";
        CommentResponseDto commentResponseDto = json.parse(jsonContent).getObject();

        assertThat(1L).isEqualTo(commentResponseDto.getId());
        assertThat("text").isEqualTo(commentResponseDto.getText());
        assertThat("name").isEqualTo(commentResponseDto.getAuthorName());
        assertThat(LocalDateTime.parse("2023-07-04T03:38:40",
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))
                .isEqualTo(commentResponseDto.getCreated());
    }
}