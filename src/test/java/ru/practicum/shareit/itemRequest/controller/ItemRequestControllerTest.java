package ru.practicum.shareit.itemRequest.controller;

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
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.itemRequest.ItemRequestController;
import ru.practicum.shareit.itemRequest.dto.ItemRequestInputDto;
import ru.practicum.shareit.itemRequest.dto.ItemRequestOutputDto;
import ru.practicum.shareit.itemRequest.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {ItemRequestController.class})
@AutoConfigureMockMvc
class ItemRequestControllerTest {
    private static final String header = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemRequestService itemRequestService;
    @Autowired
    ObjectMapper objectMapper;

    private ItemRequestOutputDto itemRequestOutputDto;
    private List<ItemRequestOutputDto> itemRequestListDto;
    private ItemRequestInputDto itemRequestInputDto;

    @BeforeEach
    void init() {
        itemRequestOutputDto = ItemRequestOutputDto.builder()
                .id(1L)
                .created(LocalDateTime.now())
                .description("test")
                .items(List.of(ItemDto.builder().id(1L).build()))
                .build();
        itemRequestInputDto = ItemRequestInputDto.builder()
                .description("test")
                .build();
        itemRequestListDto = List.of(itemRequestOutputDto);
    }

    @Test
    @SneakyThrows
    @DisplayName("getItemRequest когда пустой лист")
    void getItemRequestsWhenEmptyListTest() {
        when(itemRequestService.getItemRequest(anyLong())).thenReturn(List.of());

        mockMvc.perform(get("/requests")
                        .header(header, "1"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"))
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    @DisplayName("getItemRequest есть запросы,статус 200")
    void getItemRequestWhenListHaveOneRequests() {
        when(itemRequestService.getItemRequest(anyLong())).thenReturn(itemRequestListDto);

        mockMvc.perform(get("/requests")
                        .header(header, "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemRequestListDto)))
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    @DisplayName("getItemRequestsByOtherUsers 200 status")
    void getItemRequestsByOtherUsersWhenAllOk() {
        when(itemRequestService.getItemRequestsByIdOwner(anyLong(), anyLong())).thenReturn(itemRequestOutputDto);

        mockMvc.perform(get("/requests/{requestId}", 1L)
                        .header(header, "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemRequestOutputDto)))
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    @DisplayName("getItemRequestsByOtherUsers ItemRequest существует 200 статус")
    void getItemRequestsByOtherUsersWhenHaveOneRequestStatusOk() {
        when(itemRequestService.getItemRequestsByOtherUsers(anyLong(), anyInt(), anyInt())).thenReturn(itemRequestListDto);

        mockMvc.perform(get("/requests/all")
                        .header(header, "1")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemRequestListDto)))
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    @DisplayName("getItemRequestsByOtherUsers пустой list")
    void ggetItemRequestsByOtherUsersEmptyListOKStatus() {
        when(itemRequestService.getItemRequestsByOtherUsers(anyLong(), anyInt(), anyInt())).thenReturn(List.of());

        mockMvc.perform(get("/requests/all")
                        .header(header, "1")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"))
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    @DisplayName("createItemRequest 200 status")
    void createItemRequestWhenAllGoodTest() {
        when(itemRequestService.createItemRequest(anyLong(), any())).thenReturn(itemRequestOutputDto);

        mockMvc.perform(post("/requests")
                        .header(header, "1")
                        .content(objectMapper.writeValueAsString(itemRequestInputDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(itemRequestOutputDto)))
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    @DisplayName("createItemRequest User не найден 404")
    void createItemRequestWhenUserNotExistthenStatusNotFound() {
        when(itemRequestService.createItemRequest(any(), any())).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        mockMvc.perform(post("/requests")
                        .header(header, "123123")
                        .content(objectMapper.writeValueAsString(itemRequestInputDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andReturn();
    }
}