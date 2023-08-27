package ru.practicum.shareit.itemRequest.modelAndDto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.itemRequest.dto.ItemRequestInputDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestInputTest {
    @Autowired
    private JacksonTester<ItemRequestInputDto> json;

    @Test
    @SneakyThrows
    void testSerialize() {
        ItemRequestInputDto itemRequestPost = ItemRequestInputDto.builder()
                .description("description")
                .build();

        JsonContent<ItemRequestInputDto> write = json.write(itemRequestPost);

        assertThat(write).extractingJsonPathStringValue("$.description").isEqualTo("description");
    }

    @Test
    @SneakyThrows
    void testDeserialize() {
        String jsonContent = "{\"description\":\"description\"}";
        ItemRequestInputDto object = json.parse(jsonContent).getObject();

        assertThat("description").isEqualTo(object.getDescription());
    }
}