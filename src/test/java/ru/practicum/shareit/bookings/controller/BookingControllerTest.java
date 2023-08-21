package ru.practicum.shareit.bookings.controller;

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
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.exp.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {BookingController.class})
@AutoConfigureMockMvc
public class BookingControllerTest {
    private static final String header = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookingService bookingService;

    @Autowired
    ObjectMapper objectMapper;

    private BookingDto bookDto;
    private BookingDto bookResponseDto;
    private BookingCreateDto createDto;

    @BeforeEach
    void setUp() {
        bookDto = BookingDto.builder()
                .id(1L)
                .end(LocalDateTime.now().plusHours(1))
                .start(LocalDateTime.now())
                .booker(User.builder()
                        .id(1L)
                        .name("test")
                        .email("test@email.com")
                        .build())
                .item(Item.builder()
                        .id(1L)
                        .name("item")
                        .description("item description")
                        .available(true)
                        .owner(User.builder()
                                .id(1L)
                                .name("owner")
                                .email("owner@email.com")
                                .build())
                        .build())
                .status(BookingStatus.WAITING)
                .build();

        bookResponseDto = BookingDto.builder()
                .id(1L)
                .item(Item.builder()
                        .id(1L)
                        .name("item")
                        .description("item description")
                        .available(true)
                        .owner(User.builder()
                                .id(1L)
                                .name("owner")
                                .email("owner@email.com")
                                .build())
                        .build())
                .booker(User.builder()
                        .id(1L)
                        .name("test")
                        .email("test@email.com")
                        .build())
                .end(LocalDateTime.now().plusDays(2))
                .start(LocalDateTime.now().plusHours(1))
                .status(BookingStatus.WAITING)
                .build();

        createDto = BookingCreateDto.builder()
                .end(LocalDateTime.now().plusDays(2))
                .start(LocalDateTime.now().plusHours(1))
                .itemId(1L)
                .build();
    }

    @Test
    @SneakyThrows
    @DisplayName("readBookingById 200 status существующий Booking")
    void readBookingByIdWhenAllGoodStatusOk() {
        when(bookingService.getBookingByIdAndBooker(eq(1L), eq(1L))).thenReturn(bookDto);
        mockMvc.perform(get("/bookings/{bookingId}", 1L)
                        .header(header, 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookDto)))
                .andDo(print());
    }

    @Test
    @SneakyThrows
    @DisplayName("readBookingById 404 несуществующий Booking")
    void readBookingByIdNotExist() {
        when(bookingService.getBookingByIdAndBooker(eq(1L), eq(1L)))
                .thenThrow(EntityNotFoundException.class);
        mockMvc.perform(get("/bookings/{bookingId}", 1L)
                        .header(header, 1L))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @SneakyThrows
    @DisplayName("createBooking 200 status")
    void createBookingWhenAllGood() {
        when(bookingService.create(anyLong(), any())).thenReturn(bookResponseDto);

        mockMvc.perform(post("/bookings")
                        .header(header, 1L)
                        .content(objectMapper.writeValueAsString(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print())
                .andExpect(content().json(objectMapper.writeValueAsString(bookResponseDto)))
                .andReturn();
    }

    @Test
    @SneakyThrows
    @DisplayName("createBooking 400")
    void createBookingStatusBadRequest() {
        BookingCreateDto createDto = BookingCreateDto.builder()
                .end(LocalDateTime.now().plusHours(1))
                .start(LocalDateTime.now())
                .build();

        when(bookingService.create(anyLong(), any())).thenThrow(EntityNotFoundException.class);

        mockMvc.perform(post("/bookings")
                        .header(header, 1L)
                        .content(objectMapper.writeValueAsString(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    @DisplayName("createBooking without start/end Date")
    void createBookingWithoutStartOrEnd() {
        BookingCreateDto createDto = BookingCreateDto.builder()
                .itemId(1L)
                .build();

        when(bookingService.create(anyLong(), any())).thenThrow(EntityNotFoundException.class);

        mockMvc.perform(post("/bookings")
                        .header(header, 1L)
                        .content(objectMapper.writeValueAsString(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    @DisplayName("createBooking некорректное время бронирования")
    void createBookingWrongTimeTest() {

        when(bookingService.create(anyLong(), any())).thenThrow(
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Некорректное время бронирования"));

        mockMvc.perform(post("/bookings")
                        .header(header, 1L)
                        .content(objectMapper.writeValueAsString(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    @DisplayName("updateBooking approved=false")
    void updateBookingApprovedFalseTest() {
        when(bookingService.update(anyLong(), anyLong(), anyBoolean())).thenReturn(bookDto);

        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header(header, 1L)
                        .param("approved", "false")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookDto)))
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    @DisplayName("updateBooking approved=true")
    void updateBookingBookingApprovedTrue() {
        when(bookingService.update(anyLong(), anyLong(), anyBoolean())).thenReturn(bookDto);

        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header(header, 1L)
                        .param("approved", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookDto)))
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    @DisplayName("updateBooking not exist ID 404")
    void updateBookingIdNotTest() {
        when(bookingService.update(anyLong(), anyLong(), anyBoolean())).thenThrow(EntityNotFoundException.class);

        mockMvc.perform(patch("/bookings/{bookingId}", 999L)
                        .header(header, 1L)
                        .param("approved", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    @DisplayName("updateBooking user not exist 404")
    void updateBooking_whenInvokeWithUserNotExist_ThenStatusNotFound() {
        when(bookingService.update(anyLong(), anyLong(), anyBoolean())).thenThrow(EntityNotFoundException.class);

        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header(header, 0L)
                        .param("approved", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    @DisplayName("deleteBookingById all good 200 status")
    void deleteBookingByIdWhenAllOk() {
        mockMvc.perform(delete("/bookings/{bookingId}", 1L))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());
    }

    @Test
    @SneakyThrows
    @DisplayName("deleteBookingById wrong booking id")
    void deleteBookingByIdWrongBookingID() {
        doThrow(EntityNotFoundException.class).when(bookingService).deleteBookingById(0L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/bookings/{bookingId}", 0L))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @SneakyThrows
    @DisplayName("getUserBookings empty List")
    void getUserBookingsEmptyListTest() {
        when(bookingService.getUserBookings(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(List.of());
        mockMvc.perform(get("/bookings")
                        .header(header, 1L)
                        .param("from", "0")
                        .param("size", "10")
                        .param("state", "ALL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"))
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    @DisplayName("getUserBookings return not empty List")
    void getUserBookingsListReturnTest() {
        List<BookingDto> dtoList = List.of(bookDto);

        when(bookingService.getUserBookings(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(dtoList);
        mockMvc.perform(get("/bookings")
                        .header(header, 1L)
                        .param("from", "0")
                        .param("size", "10")
                        .param("state", "ALL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtoList)))
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    @DisplayName("getUserBookings not exist user")
    void getUserBookingsNotExistUserTest() {
        when(bookingService.getUserBookings(anyLong(), anyString(), anyInt(), anyInt())).thenThrow(EntityNotFoundException.class);
        mockMvc.perform(get("/bookings")
                        .header(header, 0L)
                        .param("from", "0")
                        .param("size", "10")
                        .param("state", "ALL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    @DisplayName("getUserBookings 200 status")
    void getBookingsByOwnerAllOkeyTest() {
        List<BookingDto> dtoList = List.of(bookDto);

        when(bookingService.getOwnerBookings(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(dtoList);
        mockMvc.perform(get("/bookings/owner")
                        .header(header, 1L)
                        .param("from", "0")
                        .param("size", "10")
                        .param("state", "ALL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtoList)))
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    @DisplayName("getBookingsByOwner пстой List ")
    void getBookingsByOwnerEmptyListReturn() {

        when(bookingService.getOwnerBookings(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(List.of());
        mockMvc.perform(get("/bookings/owner")
                        .header(header, 1L)
                        .param("from", "0")
                        .param("size", "10")
                        .param("state", "ALL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"))
                .andDo(print())
                .andReturn();
    }

    @Test
    @SneakyThrows
    @DisplayName("getBookingsByOwner не существующий user")
    void getBookingsByOwnerNotExistUser() {
        when(bookingService.getOwnerBookings(anyLong(), anyString(), anyInt(), anyInt())).thenThrow(EntityNotFoundException.class);
        mockMvc.perform(get("/bookings/owner")
                        .header(header, 0L)
                        .param("from", "0")
                        .param("size", "10")
                        .param("state", "ALL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andReturn();
    }
}