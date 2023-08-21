package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.exceptions.exp.EmailDuplicateException;
import ru.practicum.shareit.exceptions.exp.EntityNotFoundException;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {UserController.class})
@AutoConfigureMockMvc
public class UserControllerTest {
    @MockBean
    private UserService userService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private UserDto userDto;

    @BeforeEach
    void init() {
        userDto = UserDto.builder()
                .id(1L)
                .name("ivan")
                .email("ivan@yandex.ru")
                .build();
    }

    @Test
    @SneakyThrows
    @DisplayName("create валидное создание user без ошибок")
    void createCorrectTest() {
        when(userService.save(any())).thenReturn(userDto);
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("ivan"))
                .andExpect(jsonPath("$.email").value("ivan@yandex.ru"))
                .andDo(print());
    }

    @Test
    @SneakyThrows
    @DisplayName("create создание user с ошибкой")
    void createInCorrectTest() {
        when(userService.save(any())).thenThrow(EmailDuplicateException.class);
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andDo(print());
        Assertions.assertThrows(EmailDuplicateException.class,
                () -> userService.save(userDto));
    }

    @Test
    @SneakyThrows
    @DisplayName("create неверное тело запроса")
    void createIncorrectRequestBody() {
        UserDto wrongUserDto = UserDto.builder().build();
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(wrongUserDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @SneakyThrows
    @DisplayName("readUserByID при правильном ID")
    void getUserCorrectIdTest() {
        when(userService.getById(1L)).thenReturn(userDto);
        mockMvc.perform(get("/users/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("ivan"))
                .andExpect(jsonPath("$.email").value("ivan@yandex.ru"))
                .andDo(print());
    }

    @Test
    @SneakyThrows
    @DisplayName("readUserByID при неправильном ID")
    void getUserIncorrectIdTest() {
        when(userService.getById(99999L)).thenThrow(EntityNotFoundException.class);

        mockMvc.perform(get("/users/{id}", 99999L))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @SneakyThrows
    @DisplayName("getAllUser пустой List")
    void getAllUserReturnEmptyList() {
        when(userService.getAll()).thenReturn(List.of());
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of())))
                .andDo(print());
    }

    @Test
    @SneakyThrows
    @DisplayName("getAllUser возвращает List")
    void getAllUserReturnNotEmptyList() {
        when(userService.getAll()).thenReturn(List.of(userDto));
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(userDto))))
                .andExpect(jsonPath("$").isArray())
                .andDo(print());
    }

    @Test
    @SneakyThrows
    @DisplayName("update user все данные валидны 200 статус")
    void updateWhenAllCorrect() {
        when(userService.update(1L, userDto)).thenReturn(userDto);

        mockMvc.perform(patch("/users/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    @SneakyThrows
    @DisplayName("update user невалидные данные")
    void updateWhenInCorrect() {
        when(userService.update(any(), any())).thenThrow(EntityNotFoundException.class);
        mockMvc.perform(patch("/users/{userId]", 1000L)
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @SneakyThrows
    @DisplayName("update user неверный email")
    void updateWhenInCorrectEmail() {
        userDto.setEmail("sdfsdfsdfsdf");
        when(userService.update(any(), any())).thenThrow(new ResponseStatusException(HttpStatus.CONFLICT,
                "Пользователь с таким email уже существует"));

        mockMvc.perform(patch("/users/{userId}", 1L)
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andDo(print());
    }

    @Test
    @SneakyThrows
    @DisplayName("update user обновление email")
    void updateEmailStatusOK() {
        userDto.setEmail("newmail@ya.ru");
        when(userService.update(any(), any())).thenReturn(userDto);
        mockMvc.perform(patch("/users/{userId}", 1L)
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("ivan"))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()))
                .andDo(print());
    }

    @Test
    @SneakyThrows
    @DisplayName("update user имени email")
    void updateNameStatusOK() {
        userDto.setName("sdfsdf");
        when(userService.update(any(), any())).thenReturn(userDto);
        mockMvc.perform(patch("/users/{userId}", 1L)
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("sdfsdf"))
                .andExpect(jsonPath("$.email").value("ivan@yandex.ru"))
                .andDo(print());
    }

    @Test
    @SneakyThrows
    @DisplayName("delete user негативный статус")
    void deleteUserBadStatus() {
        doThrow(EntityNotFoundException.class).when(userService).delete(99999L);
        mockMvc.perform(delete("/users/{userId}", 99999L))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @SneakyThrows
    @DisplayName("delete user успешный статус")
    void deleteUserOkStatus() {
        mockMvc.perform(delete("/users/{userId}", 1L))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());
    }
}