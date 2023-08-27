package ru.practicum.shareit.bookings.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repositories.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exceptions.exp.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repositories.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    public static final long ID = 1L;
    private BookingService bookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    private BookingDto bookingDto;
    private Booking booking;
    private Item item;
    private User user;

    private BookingCreateDto bookingCreateDto;

    @BeforeEach
    void init() {
        bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository);
        user = User.builder().id(ID).build();
        item = Item.builder()
                .id(ID)
                .available(true)
                .owner(user)
                .build();
        bookingDto = BookingDto.builder()
                .id(ID)
                .booker(user)
                .status(BookingStatus.WAITING)
                .end(LocalDateTime.now().plusDays(1))
                .start(LocalDateTime.now())
                .item(item)
                .build();
        booking = Booking.builder()
                .id(ID)
                .booker(user)
                .status(BookingStatus.WAITING)
                .end(LocalDateTime.now().plusDays(1))
                .start(LocalDateTime.now())
                .item(item)
                .build();

        bookingCreateDto = BookingCreateDto.builder()
                .itemId(ID)
                .end(LocalDateTime.now().plusDays(1))
                .start(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("getBookingById 200 status")
    void getBookingByIdAndBookerAllOkeyTest() {
        when(userRepository.existsById(eq(ID))).thenReturn(true);
        when(bookingRepository.getBookingFull(ID)).thenReturn(Optional.of(booking));
        when(itemRepository.findById(ID)).thenReturn(Optional.of(item));

        BookingDto result = bookingService.getBookingByIdAndBooker(ID, user.getId());

        assertEquals(bookingDto.getId(), result.getId());
        assertEquals(bookingDto.getBooker().getId(), result.getBooker().getId());
        assertEquals(bookingDto.getStatus(), result.getStatus());
        assertEquals(bookingDto.getEnd().getSecond(), result.getEnd().getSecond());
        assertEquals(bookingDto.getStart().getSecond(), result.getStart().getSecond());
        assertEquals(bookingDto.getItem().getId(), result.getItem().getId());
        verify(bookingRepository, times(1)).getBookingFull(ID);
        verify(itemRepository, times(1)).findById(ID);
        verify(userRepository, times(1)).existsById(ID);
    }

    @Test
    @DisplayName("getBookingById неверный itemId 404 status")
    void getBookingByIdAndBookerItemNotFound() {
        when(userRepository.existsById(eq(ID))).thenReturn(true);
        when(bookingRepository.getBookingFull(ID)).thenReturn(Optional.of(booking));
        when(itemRepository.findById(ID)).thenReturn(Optional.empty());
        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> bookingService.getBookingByIdAndBooker(ID, user.getId()));

        assertEquals("Item не существует", entityNotFoundException.getMessage());
        verify(bookingRepository, times(1)).getBookingFull(ID);
        verify(userRepository, times(1)).existsById(ID);
        verify(itemRepository, times(1)).findById(ID);
    }

    @Test
    @DisplayName(" getBookingById неверный BookingID 404 status")
    void getBookingByIdBookingNotFound() {
        when(userRepository.existsById(eq(ID))).thenReturn(true);
        when(bookingRepository.getBookingFull(ID)).thenReturn(Optional.empty());
        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> bookingService.getBookingByIdAndBooker(ID, user.getId()));

        assertEquals("Booking не существует", entityNotFoundException.getMessage());
        verify(bookingRepository, times(1)).getBookingFull(ID);
        verify(userRepository, times(1)).existsById(ID);
        verify(itemRepository, times(0)).findById(ID);
    }

    @Test
    @DisplayName("getBookingByIdAndBooker user не найден")
    void getBookingByIdAndBookerUserNotFound() {
        when(userRepository.existsById(eq(ID))).thenReturn(false);
        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> bookingService.getBookingByIdAndBooker(ID, user.getId()));

        assertEquals("User не найден", entityNotFoundException.getMessage());
        verify(userRepository, times(1)).existsById(ID);
        verify(bookingRepository, times(0)).getBookingFull(ID);
        verify(itemRepository, times(0)).findById(ID);
    }

    @Test
    @DisplayName("getBookingByIdAndBooker user не owner ")
    void getBookingByIdAndBookerUserNotOwner() {
        Item badItem = Item.builder()
                .id(ID)
                .owner(User.builder().id(2L).build())
                .build();

        Booking badBooking = Booking.builder()
                .id(ID)
                .booker(User.builder().id(2L).build())
                .status(BookingStatus.WAITING)
                .end(LocalDateTime.now().plusDays(1))
                .start(LocalDateTime.now())
                .item(item)
                .build();

        when(userRepository.existsById(eq(ID))).thenReturn(true);
        when(bookingRepository.getBookingFull(ID)).thenReturn(Optional.of(badBooking));
        when(itemRepository.findById(ID)).thenReturn(Optional.of(badItem));

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> bookingService.getBookingByIdAndBooker(ID, ID));

        assertEquals("Некорректный запрос на бронирование", entityNotFoundException.getMessage());
        verify(bookingRepository, times(1)).getBookingFull(ID);
        verify(itemRepository, times(1)).findById(ID);
        verify(userRepository, times(1)).existsById(ID);
    }

    @Test
    @DisplayName("getBookingByIdAndBooker неверный запрос на бронирование")
    void getBookingByIdAndBookerWrongBooking() {
        Item badItem = Item.builder()
                .id(ID)
                .owner(User.builder().id(2L).build())
                .build();

        Booking badBooking = Booking.builder()
                .id(ID)
                .booker(User.builder().id(2L).build())
                .status(BookingStatus.WAITING)
                .end(LocalDateTime.now().plusDays(1))
                .start(LocalDateTime.now())
                .item(item)
                .build();

        when(userRepository.existsById(eq(ID))).thenReturn(true);
        when(bookingRepository.getBookingFull(ID)).thenReturn(Optional.of(badBooking));
        when(itemRepository.findById(ID)).thenReturn(Optional.of(badItem));
        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> bookingService.getBookingByIdAndBooker(ID, ID));

        assertEquals("Некорректный запрос на бронирование", entityNotFoundException.getMessage());
        verify(bookingRepository, times(1)).getBookingFull(ID);
        verify(itemRepository, times(1)).findById(ID);
        verify(userRepository, times(1)).existsById(ID);
    }

    @Test
    @DisplayName("create успешный")
    void createSuccecful() {
        Item createItem = Item.builder()
                .id(ID)
                .available(true)
                .owner(User.builder().id(2L).build())
                .build();

        when(itemRepository.findByIdFull(ID)).thenReturn(Optional.of(createItem));
        when(userRepository.findById(ID)).thenReturn(Optional.of(user));
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingDto result = bookingService.create(ID, bookingCreateDto);

        assertEquals(bookingDto.getId(), result.getId());
        assertEquals(bookingDto.getBooker().getId(), result.getBooker().getId());
        assertEquals(bookingDto.getStatus(), result.getStatus());
        assertEquals(bookingDto.getEnd().getSecond(), result.getEnd().getSecond());
        assertEquals(bookingDto.getStart().getSecond(), result.getStart().getSecond());
        assertEquals(bookingDto.getItem().getId(), result.getItem().getId());
        verify(bookingRepository, times(1)).save(any());
        verify(itemRepository, times(1)).findByIdFull(ID);
        verify(userRepository, times(1)).findById(ID);
    }

    @Test
    @DisplayName("create user не найден")
    void createUserNotFound() {
        Item createItem = Item.builder()
                .id(ID)
                .available(true)
                .owner(User.builder().id(2L).build())
                .build();

        when(itemRepository.findByIdFull(ID)).thenReturn(Optional.of(createItem));
        when(userRepository.findById(ID)).thenReturn(Optional.empty());

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> bookingService.create(ID, bookingCreateDto));

        assertEquals("User не найден", entityNotFoundException.getMessage());
        verify(bookingRepository, times(0)).save(any());
        verify(itemRepository, times(1)).findByIdFull(ID);
        verify(userRepository, times(1)).findById(ID);
    }

    @Test
    @DisplayName("create item на существует")
    void createItemNotFound() {
        when(itemRepository.findByIdFull(ID)).thenReturn(Optional.empty());

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> bookingService.create(ID, bookingCreateDto));

        assertEquals("Item не существует", entityNotFoundException.getMessage());
        verify(bookingRepository, times(0)).save(any());
        verify(userRepository, times(0)).findById(ID);
        verify(itemRepository, times(1)).findByIdFull(ID);
    }

    @Test
    @DisplayName("create availbale=false 400 status")
    void createAvailableFalse() {
        Item createItem = Item.builder()
                .id(ID)
                .available(false)
                .owner(User.builder().id(2L).build())
                .build();

        when(itemRepository.findByIdFull(ID)).thenReturn(Optional.of(createItem));
        when(userRepository.findById(ID)).thenReturn(Optional.of(user));
        ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class,
                () -> bookingService.create(ID, bookingCreateDto));

        assertEquals("400 BAD_REQUEST \"Item недоступен\"", responseStatusException.getMessage());
        verify(bookingRepository, times(0)).save(any());
        verify(itemRepository, times(1)).findByIdFull(ID);
        verify(userRepository, times(1)).findById(ID);
    }

    @Test
    @DisplayName("create неверный owner")
    void createOwnerError() {
        Item createItem = Item.builder()
                .id(ID)
                .available(true)
                .owner(User.builder().id(1L).build())
                .build();

        when(itemRepository.findByIdFull(ID)).thenReturn(Optional.of(createItem));
        when(userRepository.findById(ID)).thenReturn(Optional.of(user));
        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> bookingService.create(ID, bookingCreateDto));

        assertEquals("Недостаточно прав для создания Booking", entityNotFoundException.getMessage());
        verify(bookingRepository, times(0)).save(any());
        verify(itemRepository, times(1)).findByIdFull(ID);
        verify(userRepository, times(1)).findById(ID);
    }

    @Test
    @DisplayName("create ошибка времени")
    void createTimeError() {
        Item createItem = Item.builder()
                .id(ID)
                .available(true)
                .owner(User.builder().id(2L).build())
                .build();

        LocalDateTime now = LocalDateTime.now();
        BookingCreateDto badBooking = BookingCreateDto.builder()
                .itemId(ID)
                .end(now)
                .start(now)
                .build();

        when(itemRepository.findByIdFull(ID)).thenReturn(Optional.of(createItem));
        when(userRepository.findById(ID)).thenReturn(Optional.of(user));
        ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class,
                () -> bookingService.create(ID, badBooking));

        assertEquals("400 BAD_REQUEST \"Некорректное время бронирования\"", responseStatusException.getMessage());
        verify(bookingRepository, times(0)).save(any());
        verify(itemRepository, times(1)).findByIdFull(ID);
        verify(userRepository, times(1)).findById(ID);
    }

    @Test
    @DisplayName("update approved=false")
    void updateApprovedFalse() {
        User userPatch = User.builder().id(4L).email("email@email.com").name("name").build();
        Item itemPatch = Item.builder().id(1L).available(true)
                .owner(User.builder()
                        .id(ID)
                        .email("email2@email.com").name("name2")
                        .build())
                .build();
        Booking bookingPatch = Booking.builder()
                .id(ID)
                .booker(userPatch)
                .status(BookingStatus.WAITING)
                .end(LocalDateTime.now().plusDays(1))
                .start(LocalDateTime.now())
                .item(itemPatch)
                .build();

        Booking bookingSaved = Booking.builder()
                .id(ID)
                .booker(userPatch)
                .status(BookingStatus.REJECTED)
                .end(LocalDateTime.now().plusDays(1))
                .start(LocalDateTime.now())
                .item(itemPatch)
                .build();

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.getBookingFull(anyLong())).thenReturn(Optional.of(bookingPatch));
        when(bookingRepository.save(any())).thenReturn(bookingSaved);

        BookingDto result = bookingService.update(ID, ID, Boolean.FALSE);

        assertEquals(BookingStatus.REJECTED, result.getStatus());
        verify(userRepository, times(1)).existsById(any());
        verify(bookingRepository, times(1)).getBookingFull(any());
        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("update approved=true")
    void updateApprovedTrue() {
        User userPatch = User.builder().id(4L).email("email@email.com").name("name").build();
        Item itemPatch = Item.builder().id(1L).available(true)
                .owner(User.builder()
                        .id(ID)
                        .email("email2@email.com").name("name2")
                        .build())
                .build();
        Booking bookingPatch = Booking.builder()
                .id(ID)
                .booker(userPatch)
                .status(BookingStatus.WAITING)
                .end(LocalDateTime.now().plusDays(1))
                .start(LocalDateTime.now())
                .item(itemPatch)
                .build();

        Booking bookingSaved = Booking.builder()
                .id(ID)
                .booker(userPatch)
                .status(BookingStatus.APPROVED)
                .end(LocalDateTime.now().plusDays(1))
                .start(LocalDateTime.now())
                .item(itemPatch)
                .build();

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.getBookingFull(anyLong())).thenReturn(Optional.of(bookingPatch));
        when(bookingRepository.save(any())).thenReturn(bookingSaved);

        BookingDto result = bookingService.update(ID, ID, Boolean.TRUE);

        assertEquals(BookingStatus.APPROVED, result.getStatus());
        verify(userRepository, times(1)).existsById(any());
        verify(bookingRepository, times(1)).getBookingFull(any());
        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("update попытка отредактировать вещь не собственником")
    void updageItemErrorIdUser() {
        User userPatch = User.builder().id(4L).email("email@email.com").name("name").build();
        Item itemPatch = Item.builder().id(1L).available(true)
                .owner(userPatch)
                .build();
        Booking bookingPatch = Booking.builder()
                .id(ID)
                .booker(userPatch)
                .status(BookingStatus.WAITING)
                .end(LocalDateTime.now().plusDays(1))
                .start(LocalDateTime.now())
                .item(itemPatch)
                .build();

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.getBookingFull(anyLong())).thenReturn(Optional.of(bookingPatch));
        ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class,
                () -> bookingService.update(ID, ID, Boolean.FALSE));

        assertEquals("400 BAD_REQUEST \"Данный User не может редактировать Booking\"", responseStatusException.getMessage());
        verify(userRepository, times(1)).existsById(any());
        verify(bookingRepository, times(1)).getBookingFull(any());
        verify(bookingRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("uodate неверный id букинга")
    void updateBookingWrongIdUser() {
        Booking bookingPatch = Booking.builder()
                .id(ID)
                .booker(user)
                .status(BookingStatus.WAITING)
                .end(LocalDateTime.now().plusDays(1))
                .start(LocalDateTime.now())
                .item(item)
                .build();

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.getBookingFull(anyLong())).thenReturn(Optional.of(bookingPatch));
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> bookingService.update(ID, ID, Boolean.FALSE));

        assertEquals("данный User не может редактировать Booking", exception.getMessage());
        verify(userRepository, times(1)).existsById(any());
        verify(bookingRepository, times(1)).getBookingFull(any());
        verify(bookingRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("update статус не waiting")
    void updateBookingStatusNotWaiting() {
        Booking bookingPatch = Booking.builder()
                .id(ID)
                .booker(user)
                .status(BookingStatus.REJECTED)
                .end(LocalDateTime.now().plusDays(1))
                .start(LocalDateTime.now())
                .item(item)
                .build();

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.getBookingFull(anyLong())).thenReturn(Optional.of(bookingPatch));
        ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class,
                () -> bookingService.update(ID, ID, Boolean.FALSE));

        assertEquals("400 BAD_REQUEST \"Booking не находится в статусе WAITING\"", responseStatusException.getMessage());
        verify(userRepository, times(1)).existsById(any());
        verify(bookingRepository, times(1)).getBookingFull(any());
        verify(bookingRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("update неверный Booking")
    void updateBookingNotFound() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.getBookingFull(anyLong())).thenReturn(Optional.empty());

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> bookingService.update(ID, ID, Boolean.FALSE));

        assertEquals("Booking не найден", entityNotFoundException.getMessage());
        verify(userRepository, times(1)).existsById(any());
        verify(bookingRepository, times(1)).getBookingFull(any());
        verify(bookingRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("deleteBookingById успешное удаление")
    void deleteBookingByIdWhenItExist() {
        when(bookingRepository.existsById(eq(1L))).thenReturn(true);

        bookingService.deleteBookingById(ID);

        verify(bookingRepository, times(1)).existsById(anyLong());
        verify(bookingRepository, times(1)).deleteById(anyLong());
    }

    @Test
    @DisplayName("deleteBookingById booking не найден")
    void deleteBookingByBookingNotExist() {
        when(bookingRepository.existsById(eq(ID))).thenReturn(false);
        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> bookingService.deleteBookingById(ID));

        assertEquals("Booking не существует.", entityNotFoundException.getMessage());
        verify(bookingRepository, times(1)).existsById(anyLong());
        verify(bookingRepository, times(0)).deleteById(anyLong());
    }

    @Test
    @DisplayName("etUserBookings успешный вызов")
    void getUserBookingsSuccesfullCall() {
        BookingState bookingState = BookingState.PAST;

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findByBookerIdAndEndLessThanOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> bookings = bookingService.getUserBookings(ID, bookingState.toString(), 0, 2);

        assertEquals(1, bookings.size());
        verify(bookingRepository, times(1))
                .findByBookerIdAndEndLessThanOrderByStartDesc(anyLong(), any(), any());
        verify(userRepository, times(1)).existsById(anyLong());
    }

    @Test
    @DisplayName("getUserBookings успешеый вызов state=current")
    void getUserBookingsStateCurrent() {
        BookingState bookingState = BookingState.CURRENT;

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository
                .findByBookerIdAndStartLessThanAndEndGreaterThanOrderByStartDesc(anyLong(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> bookings = bookingService.getUserBookings(ID, bookingState.toString(), 0, 2);

        assertEquals(1, bookings.size());
        verify(bookingRepository, times(1))
                .findByBookerIdAndStartLessThanAndEndGreaterThanOrderByStartDesc(anyLong(), any(), any(), any());
        verify(userRepository, times(1)).existsById(anyLong());
    }

    @Test
    @DisplayName("getUserBookings успешеый вызов state=future")
    void getUserBookingsStateFuture() {
        BookingState bookingState = BookingState.FUTURE;

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository
                .findByBookerIdAndStartGreaterThanOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> bookings = bookingService.getUserBookings(ID, bookingState.toString(), 0, 2);

        assertEquals(1, bookings.size());
        verify(bookingRepository, times(1))
                .findByBookerIdAndStartGreaterThanOrderByStartDesc(anyLong(), any(), any());
        verify(userRepository, times(1)).existsById(anyLong());
    }

    @Test
    @DisplayName("getUserBookings успешеый вызов state=waiting")
    void getUserBookingsStateWaiting() {
        BookingState bookingState = BookingState.WAITING;

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository
                .findByBookerIdAndStatusOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> bookings = bookingService.getUserBookings(ID, bookingState.toString(), 0, 2);

        assertEquals(1, bookings.size());
        verify(bookingRepository, times(1))
                .findByBookerIdAndStatusOrderByStartDesc(anyLong(), any(), any());
        verify(userRepository, times(1)).existsById(anyLong());
    }

    @Test
    @DisplayName("getUserBookings успешеый вызов state=rejected")
    void getUserBookingsStateRejected() {
        BookingState bookingState = BookingState.REJECTED;

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository
                .findByBookerIdAndStatusOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> bookings = bookingService.getUserBookings(ID, bookingState.toString(), 0, 2);

        assertEquals(1, bookings.size());
        verify(bookingRepository, times(1))
                .findByBookerIdAndStatusOrderByStartDesc(anyLong(), any(), any());
        verify(userRepository, times(1)).existsById(anyLong());
    }

    @Test
    @DisplayName("getUserBookings успешеый вызов state=all")
    void getUserBookingsStateAll() {
        BookingState bookingState = BookingState.ALL;

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository
                .findByBookerIdOrderByStartDesc(anyLong(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> bookings = bookingService.getUserBookings(ID, bookingState.toString(), 0, 2);

        assertEquals(1, bookings.size());
        verify(bookingRepository, times(1))
                .findByBookerIdOrderByStartDesc(anyLong(), any());
        verify(userRepository, times(1)).existsById(anyLong());
    }

    @Test
    @DisplayName("getUserBookings несуществеющий статус")
    void getUserBookingsInvalidState() {
        String state = "INVALID";
        when(userRepository.existsById(anyLong())).thenReturn(true);
        ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class,
                () -> bookingService.getUserBookings(ID, state, 0, 2));

        assertEquals("400 BAD_REQUEST \"Unknown state: " + state + "\"", responseStatusException.getMessage());
        verify(bookingRepository, times(0))
                .findByBookerIdOrderByStartDesc(anyLong(), any());
        verify(userRepository, times(1)).existsById(anyLong());
    }

    @Test
    @DisplayName("getOwnerBookings State=Current")
    void getOwnerBookingsStateCurrent() {
        BookingState bookingState = BookingState.CURRENT;

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository
                .findByItemOwnerAndStartLessThanAndEndGreaterThanOrderByStartDesc(anyLong(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> bookings = bookingService.getOwnerBookings(ID, bookingState.toString(), 0, 2);

        assertEquals(1, bookings.size());
        verify(bookingRepository, times(1))
                .findByItemOwnerAndStartLessThanAndEndGreaterThanOrderByStartDesc(anyLong(), any(), any(), any());
        verify(userRepository, times(1)).existsById(anyLong());
    }

    @Test
    @DisplayName("getOwnerBookings State=past")
    void getOwnerBookingsStatePast() {
        BookingState bookingState = BookingState.PAST;

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository
                .findByItemOwnerAndEndLessThanOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> bookings = bookingService.getOwnerBookings(ID, bookingState.toString(), 0, 2);

        assertEquals(1, bookings.size());
        verify(bookingRepository, times(1))
                .findByItemOwnerAndEndLessThanOrderByStartDesc(anyLong(), any(), any());
        verify(userRepository, times(1)).existsById(anyLong());
    }

    @Test
    @DisplayName("getOwnerBookings State=future")
    void getOwnerBookingsStateFuture() {
        BookingState bookingState = BookingState.FUTURE;

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository
                .findByItemOwnerAndStartGreaterThanOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> bookings = bookingService.getOwnerBookings(ID, bookingState.toString(), 0, 2);

        assertEquals(1, bookings.size());
        verify(bookingRepository, times(1))
                .findByItemOwnerAndStartGreaterThanOrderByStartDesc(anyLong(), any(), any());
        verify(userRepository, times(1)).existsById(anyLong());
    }

    @Test
    @DisplayName("getOwnerBookings State=waiting")
    void getOwnerBookingsStateWaiting() {
        BookingState bookingState = BookingState.WAITING;

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository
                .findByItemOwnerAndStatusOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> bookings = bookingService.getOwnerBookings(ID, bookingState.toString(), 0, 2);

        assertEquals(1, bookings.size());
        verify(bookingRepository, times(1))
                .findByItemOwnerAndStatusOrderByStartDesc(anyLong(), any(), any());
        verify(userRepository, times(1)).existsById(anyLong());
    }

    @Test
    @DisplayName("getOwnerBookings State=rejected")
    void getOwnerBookingsStateRejected() {
        BookingState bookingState = BookingState.REJECTED;

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository
                .findByItemOwnerAndStatusOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> bookings = bookingService.getOwnerBookings(ID, bookingState.toString(), 0, 2);

        assertEquals(1, bookings.size());
        verify(bookingRepository, times(1))
                .findByItemOwnerAndStatusOrderByStartDesc(anyLong(), any(), any());
        verify(userRepository, times(1)).existsById(anyLong());
    }

    @Test
    @DisplayName("getOwnerBookings State=all")
    void getOwnerBookingsStateAll() {
        BookingState bookingState = BookingState.ALL;

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository
                .findByItemOwnerOrderByStartDesc(anyLong(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingDto> bookings = bookingService.getOwnerBookings(ID, bookingState.toString(), 0, 2);

        assertEquals(1, bookings.size());
        verify(bookingRepository, times(1))
                .findByItemOwnerOrderByStartDesc(anyLong(), any());
        verify(userRepository, times(1)).existsById(anyLong());
    }

    @Test
    void getOwnerBookings_whenStateNotExist_thenThrowBookingUnsupportedStateException() {
        String state = "INVALID";

        when(userRepository.existsById(anyLong())).thenReturn(true);

        ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class,
                () -> bookingService.getOwnerBookings(ID, state, 0, 2));

        assertEquals("400 BAD_REQUEST \"Unknown state: " + state + "\"", responseStatusException.getMessage());
        verify(bookingRepository, times(0))
                .findByItemOwnerOrderByStartDesc(anyLong(), any());
        verify(userRepository, times(1)).existsById(anyLong());
    }
}