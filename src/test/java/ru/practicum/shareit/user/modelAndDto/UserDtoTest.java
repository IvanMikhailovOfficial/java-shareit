package ru.practicum.shareit.user.modelAndDto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserDtoTest {
    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    @SneakyThrows
    void testSerialize() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("name")
                .email("email@email.com")
                .build();

        JsonContent<UserDto> write = json.write(userDto);

        assertThat(write).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(write).extractingJsonPathStringValue("$.name").isEqualTo("name");
        assertThat(write).extractingJsonPathStringValue("$.email").isEqualTo("email@email.com");
    }

    @Test
    @SneakyThrows
    void testDeserialize() {
        String jsonContent = "{\"id\":1,\"name\":\"name\",\"email\":\"email@email.com\"}";
        UserDto object = json.parse(jsonContent).getObject();

        assertThat("name").isEqualTo(object.getName());
        assertThat(1L).isEqualTo(object.getId());
        assertThat("email@email.com").isEqualTo(object.getEmail());
    }
}