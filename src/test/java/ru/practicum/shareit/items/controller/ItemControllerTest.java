package ru.practicum.shareit.items.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.comments.dto.CommentRequestDto;
import ru.practicum.shareit.comments.dto.CommentResponseDto;
import ru.practicum.shareit.exceptions.exp.EntityNotFoundException;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {ItemController.class})
@AutoConfigureMockMvc
public class ItemControllerTest {

    private final String header = "X-Sharer-User-Id";
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemService itemService;

    @Autowired
    ObjectMapper objectMapper;
    private ItemResponseDto itemResponseDto;
    private ItemDto itemDto;
    private List<ItemResponseDto> itemDtoList;
    private CommentResponseDto commentResponseDto;
    private CommentRequestDto commentRequestDto;

    @BeforeEach
    void init() {

        commentResponseDto = CommentResponseDto.builder()
                .created(LocalDateTime.MAX)
                .authorName("111")
                .text("12312")
                .id(1L)
                .build();

        itemDto = ItemDto.builder()
                .id(1L)
                .name("123")
                .requestId(1L)
                .description("123")
                .available(true)
                .build();
        itemDtoList = new ArrayList<>();
        itemResponseDto = ItemResponseDto.builder()
                .id(1)
                .name("ivan")
                .available(true)
                .comments(List.of())
                .description("123")
                .build();
        ItemDto.builder()
                .available(true)
                .description("123")
                .id(1L)
                .name("123")
                .requestId(1L)
                .build();
        CommentRequestDto.builder()
                .text("123")
                .build();
        CommentResponseDto.builder()
                .authorName("123")
                .text("123")
                .created(LocalDateTime.MAX)
                .id(1L)
                .build();
    }

    @Test
    @SneakyThrows
    @DisplayName("gettAllItems 200 статус и пустой list")
    void getAllItemsEmptyListTest() {
        when(itemService.findAllItemByUserId(anyLong()))
                .thenReturn(List.of());
        mockMvc.perform(get("/items")
                        .header(header, 1L)
                        .param("from", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"))
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    @DisplayName("gettAllItems не пустой list")
    void getAllItemsNotEmptyListOKStatus() {
        itemDtoList.add(itemResponseDto);
        when(itemService.findAllItemByUserId(anyLong())).thenReturn(itemDtoList);

        mockMvc.perform(get("/items")
                        .header(header, 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemDtoList)))
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    @DisplayName("gettAllItems не существующий user")
    void getAllItemsNotValidUser() {
        when(itemService.findAllItemByUserId(anyLong())).thenThrow(EntityNotFoundException.class);

        mockMvc.perform(get("/items")
                        .header(header, 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andReturn();
    }

    @SneakyThrows
    @Test
    @DisplayName("readItemById получения объекта,когда он существует")
    void readItemByIDWhenInvokedCorrect() {
        when(itemService.findById(anyLong(), anyLong())).thenReturn(itemResponseDto);

        mockMvc.perform(get("/items/{itemID}", 1L)
                        .header(header, 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemResponseDto)))
                .andDo(print())
                .andReturn();
    }

    @SneakyThrows
    @Test
    @DisplayName("readItemById не существующий User")
    void readItemByIdWhenUserNotExist() {
        when(itemService.findById(anyLong(), anyLong())).thenThrow(EntityNotFoundException.class);

        mockMvc.perform(get("/items/{itemId}", 1L)
                        .header(header, 111111L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @SneakyThrows
    @Test
    @DisplayName("createItem 200 status")
    void createItemTest() {
        when(itemService.save(anyLong(), any())).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header(header, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
    }

    @SneakyThrows
    @Test
    @DisplayName("createItem user не сущестует")
    void createItemNotExistUserTest() {
        when(itemService.save(any(), any())).thenThrow(EntityNotFoundException.class);

        mockMvc.perform(post("/items")
                        .header(header, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andReturn();
    }

    @SneakyThrows
    @Test
    @DisplayName("createItem user without name")
    void createItemWithoutNameTest() {
        when(itemService.save(any(), any())).thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Заголовок айди юзера не найден"));

        mockMvc.perform(post("/items")
                        .header(header, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn();
    }

    @SneakyThrows
    @Test
    @DisplayName("updateItem существующего ")
    void updateItemCorrectTest() {
        when(itemService.patch(anyLong(), anyLong(), any())).thenReturn(itemDto);

        mockMvc.perform(patch("/items/{itemId}", 1L)
                        .header(header, 1L)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemDto)))
                .andDo(print())
                .andReturn();
    }

    @SneakyThrows
    @Test
    @DisplayName("updateItem несуществующий Item ")
    void updateItemNotExistItemTest() {

        when(itemService.patch(anyLong(), anyLong(), any())).thenThrow(EntityNotFoundException.class);

        mockMvc.perform(patch("/items/{itemId}", 999999)
                        .header(header, 1L)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andReturn();
    }

    @SneakyThrows
    @Test
    @DisplayName("deleteItemById несуществующий Item ")
    void deleteItemByNotExistId() {
        doThrow(EntityNotFoundException.class).when(itemService).delete(9999L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/items/{itemId}", 9999L))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @SneakyThrows
    @Test
    @DisplayName("deleteItemById несуществующий Item ")
    void deleteItemById() {
        mockMvc.perform(delete("/items/{itemId}", 1L))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());
    }

    @SneakyThrows
    @Test
    @DisplayName("searchItemsByText text пустой ")
    void searchItemsByTextWhenTextEmpty() {
        when(itemService.findByText(any())).thenReturn(List.of());

        mockMvc.perform(get("/items/search")
                        .param("text", "text"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"))
                .andDo(print())
                .andReturn();
    }

    @SneakyThrows
    @Test
    @DisplayName("addComment успешное добавление ")
    void addComment() {
        when(itemService.addComment(anyLong(), anyLong(), any())).thenReturn(commentResponseDto);

        mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .header(header, 1L)
                        .content(objectMapper.writeValueAsString(commentResponseDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(commentResponseDto)))
                .andDo(print())
                .andReturn();
    }
}