package ru.practicum.shareit.items.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingDtoUser;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repositories.BookingRepository;
import ru.practicum.shareit.comments.dto.CommentRequestDto;
import ru.practicum.shareit.comments.dto.CommentResponseDto;
import ru.practicum.shareit.comments.model.Comment;
import ru.practicum.shareit.comments.repositories.CommentRepository;
import ru.practicum.shareit.exceptions.exp.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repositories.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    public static final long TEST_ID = 1L;
    private ItemService itemService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    private Item item;
    private ItemDto itemDto;
    private ItemResponseDto itemResponseDto;
    private User user;
    private Comment comment;

    @BeforeEach
    void init() {
        itemService = new ItemServiceImpl(itemRepository, userRepository, bookingRepository, commentRepository);
        itemDto = ItemDto.builder()
                .name("test")
                .comments(List.of())
                .owner(User.builder().build())
                .requestId(TEST_ID)
                .description("test")
                .available(true)
                .lastBooking(BookingDtoUser.builder().id(TEST_ID).build())
                .nextBooking(BookingDtoUser.builder().id(2L).build())
                .build();

        itemResponseDto = ItemResponseDto.builder()
                .id(TEST_ID)
                .name("test")
                .comments(List.of())
                .requestId(TEST_ID)
                .description("test")
                .available(true)
                .lastBooking(BookingResponseDto.builder().id(TEST_ID).build())
                .nextBooking(BookingResponseDto.builder().id(2L).build())
                .build();

        item = Item.builder()
                .id(TEST_ID)
                .owner(User.builder().id(TEST_ID).build())
                .name("test")
                .requestId(TEST_ID)
                .description("test")
                .available(true)
                .build();

        user = User.builder().id(1L).build();

        comment = Comment.builder()
                .id(TEST_ID)
                .author(user)
                .item(item)
                .text("test")
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("save успешный")
    void save_whenInvoked_thenItemSaved() {
        when(userRepository.findById(eq(TEST_ID))).thenReturn(Optional.of(User.builder().build()));
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto result = itemService.save(TEST_ID, itemDto);

        assertEquals(itemResponseDto.getId(), result.getId());
        assertEquals(itemResponseDto.getName(), result.getName());
        assertEquals(itemResponseDto.getDescription(), result.getDescription());
        assertEquals(itemResponseDto.getAvailable(), result.getAvailable());
        assertEquals(itemResponseDto.getRequestId(), result.getRequestId());
        verify(userRepository, times(1)).findById(TEST_ID);
        verify(itemRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("save user не существует")
    void save_whenUserNotExist() {
        when(userRepository.findById(eq(TEST_ID))).thenReturn(Optional.empty());

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> itemService.save(TEST_ID, itemDto));

        assertEquals("Пользователь не найден", entityNotFoundException.getMessage());
        verify(userRepository, times(1)).findById(TEST_ID);
        verify(itemRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("save userId=null")
    void save_whenUserIdNull() {
        ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class,
                () -> itemService.save(null, itemDto));

        assertEquals("400 BAD_REQUEST \"Заголовок айди юзера не найден\"", responseStatusException.getMessage());
        verify(userRepository, times(0)).findById(TEST_ID);
        verify(itemRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("patch успешный")
    void patch_whenInvoked_thenItemUpdated() {
        Item updatedItem = Item.builder()
                .id(TEST_ID)
                .name("updated")
                .available(false)
                .description("updated")
                .requestId(TEST_ID)
                .build();

        ItemDto updatedItemDto = ItemDto.builder()
                .id(TEST_ID)
                .name("updated")
                .description("updated")
                .available(false)
                .requestId(TEST_ID)
                .build();
        when(itemRepository.findById(eq(TEST_ID))).thenReturn(Optional.of(item));
        when(itemRepository.findByIdFull(TEST_ID)).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenReturn(updatedItem);

        ItemDto result = itemService.patch(TEST_ID, TEST_ID, updatedItemDto);

        assertEquals(updatedItemDto.getName(), result.getName());
        assertEquals(updatedItemDto.getDescription(), result.getDescription());
        assertEquals(updatedItemDto.getAvailable(), result.getAvailable());
        assertEquals(updatedItemDto.getRequestId(), result.getRequestId());
        verify(itemRepository, times(1)).findByIdFull(TEST_ID);
        verify(itemRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("patch ошибка во вреия обновления")
    void patch_whenErrorParametersItem() {
        ItemDto updatedItemDto = ItemDto.builder()
                .id(TEST_ID)
                .name("updated")
                .description("updated")
                .available(false)
                .build();

        when(itemRepository.findById(eq(TEST_ID))).thenReturn(Optional.of(item));
        when(itemRepository.findByIdFull(TEST_ID)).thenReturn(Optional.empty());

        ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class,
                () -> itemService.patch(TEST_ID, TEST_ID, updatedItemDto));

        assertEquals("400 BAD_REQUEST \"Ошибка обновления\"", responseStatusException.getMessage());
        verify(itemRepository, times(1)).findById(TEST_ID);
        verify(itemRepository, times(1)).findByIdFull(TEST_ID);
        verify(itemRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("patch user не собственник")
    void patch_whenUserNotOwnerItem() {
        ItemDto updatedItemDto = ItemDto.builder()
                .id(TEST_ID)
                .name("updated")
                .description("updated")
                .available(false)
                .build();

        when(itemRepository.findById(eq(TEST_ID))).thenReturn(Optional.of(item));

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> itemService.patch(TEST_ID, 2L, updatedItemDto));

        assertEquals("Вещь не найдена у Юзера", entityNotFoundException.getMessage());
        verify(itemRepository, times(1)).findById(TEST_ID);
        verify(itemRepository, times(0)).findByIdFull(TEST_ID);
        verify(itemRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("findById успешное")
    void findByI_whenInvoked_thenReturnItem() {
        when(itemRepository.findByIdFull(TEST_ID)).thenReturn(Optional.of(item));
        when(commentRepository.getCommentsByItem_idOrderByCreatedDesc(eq(TEST_ID))).thenReturn(List.of());
        when(bookingRepository
                .findTop1BookingByItemIdAndEndIsBeforeAndStatusIs(eq(TEST_ID), any(), any(), any()))
                .thenReturn(List.of());
        when(bookingRepository
                .findTop1BookingByItemIdAndEndIsAfterAndStatusIs(eq(TEST_ID), any(), any(), any()))
                .thenReturn(List.of());

        ItemResponseDto result = itemService.findById(TEST_ID, TEST_ID);

        assertEquals(itemResponseDto.getId(), result.getId());
        assertEquals(itemResponseDto.getName(), result.getName());
        assertEquals(itemResponseDto.getDescription(), result.getDescription());
        assertEquals(itemResponseDto.getAvailable(), result.getAvailable());
        assertEquals(itemResponseDto.getRequestId(), result.getRequestId());
        verify(itemRepository, times(1)).findByIdFull(TEST_ID);
        verify(commentRepository, times(1)).getCommentsByItem_idOrderByCreatedDesc(eq(TEST_ID));
        verify(bookingRepository, times(1)).findTop1BookingByItemIdAndEndIsBeforeAndStatusIs(
                eq(TEST_ID), any(), any(), any());
        verify(bookingRepository, times(1)).findTop1BookingByItemIdAndEndIsAfterAndStatusIs(
                eq(TEST_ID), any(), any(), any());
    }

    @Test
    @DisplayName("findById item не существует")
    void findByI_whenItemNOtExist() {
        when(itemRepository.findByIdFull(TEST_ID)).thenReturn(Optional.empty());

        EntityNotFoundException itemNotFoundException = assertThrows(EntityNotFoundException.class, () ->
                itemService.findById(TEST_ID, TEST_ID));

        assertEquals("Вещь не найдена", itemNotFoundException.getMessage());
        verify(itemRepository, times(1)).findByIdFull(TEST_ID);
        verify(commentRepository, times(0)).getCommentsByItem_idOrderByCreatedDesc(eq(TEST_ID));
        verify(bookingRepository, times(0)).findTop1BookingByItemIdAndEndIsBeforeAndStatusIs(
                eq(TEST_ID), any(), any(), any());
        verify(bookingRepository, times(0)).findTop1BookingByItemIdAndEndIsAfterAndStatusIs(
                eq(TEST_ID), any(), any(), any());
    }

    @Test
    @DisplayName("delete успешное")
    void delete_whenInvoked_thenDeleted() {
        when(itemRepository.existsById(TEST_ID)).thenReturn(true);

        itemService.delete(TEST_ID);

        verify(itemRepository, times(1)).existsById(TEST_ID);
        verify(itemRepository, times(1)).deleteById(TEST_ID);
    }

    @Test
    @DisplayName("delete item не существует")
    void delete_whenItemNotExist() {
        when(itemRepository.existsById(TEST_ID)).thenReturn(false);
        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> itemService.delete(TEST_ID));

        assertEquals("Вещь не найдена", entityNotFoundException.getMessage());
        verify(itemRepository, times(1)).existsById(TEST_ID);
        verify(itemRepository, times(0)).deleteById(TEST_ID);
    }

    @Test
    @DisplayName("findAllItemByUserId user не существует")
    void findAllItemByUserId_whenUserNotExist() {
        when(userRepository.findById(eq(TEST_ID))).thenReturn(Optional.empty());

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> itemService.findAllItemByUserId(TEST_ID));

        assertEquals("Пользователь не найден", entityNotFoundException.getMessage());
        verify(itemRepository, times(0)).findAllByOwnerIdOrderByIdAsc(eq(TEST_ID));
        verify(commentRepository, times(0)).getCommentsByItem_idOrderByCreatedDesc(eq(TEST_ID));
        verify(bookingRepository, times(0))
                .findFirstByItem_idAndEndBeforeOrderByEndDesc(eq(TEST_ID), any());
        verify(bookingRepository, times(0))
                .findFirstByItem_idAndStartAfterOrderByStartAsc(eq(TEST_ID), any());
        verify(userRepository, times(1)).findById(eq(TEST_ID));
    }

    @Test
    @DisplayName("addComment успешный")
    void addComment_whenInvoked_thenCommentSaved() {
        Booking booking = Booking.builder()
                .id(1L)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .build();

        when(userRepository.findById(TEST_ID)).thenReturn(Optional.of(user));
        when(itemRepository.findByIdFull(TEST_ID)).thenReturn(Optional.of(item));
        when(bookingRepository.findByItem_IdAndBooker_IdOrderByStartDesc(TEST_ID, TEST_ID))
                .thenReturn(List.of(booking));
        when(commentRepository.save(any())).thenReturn(comment);

        CommentResponseDto test = itemService.addComment(TEST_ID, TEST_ID,
                CommentRequestDto.builder().text("test").build());

        assertEquals(comment.getText(), test.getText());
        assertEquals(comment.getAuthor().getName(), test.getAuthorName());
        verify(userRepository, times(1)).findById(TEST_ID);
        verify(itemRepository, times(1)).findByIdFull(TEST_ID);
        verify(bookingRepository, times(1))
                .findByItem_IdAndBooker_IdOrderByStartDesc(TEST_ID, TEST_ID);
        verify(commentRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("addComment user не найден")
    void addCommentUserNotExis() {
        when(userRepository.findById(TEST_ID)).thenReturn(Optional.empty());
        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> itemService.addComment(TEST_ID, TEST_ID, CommentRequestDto.builder().text("test").build()));

        assertEquals("Пользователь не найден", entityNotFoundException.getMessage());
        verify(userRepository, times(1)).findById(TEST_ID);
        verify(itemRepository, times(0)).findByIdFull(TEST_ID);
        verify(bookingRepository, times(0))
                .findByItem_IdAndBooker_IdOrderByStartDesc(TEST_ID, TEST_ID);
        verify(commentRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("addComment item не найден")
    void addCommentItemNotExist() {
        when(userRepository.findById(TEST_ID)).thenReturn(Optional.of(user));
        when(itemRepository.findByIdFull(TEST_ID)).thenReturn(Optional.empty());

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> itemService.addComment(TEST_ID, TEST_ID, CommentRequestDto.builder().text("test").build()));

        assertEquals("Вещь не найдена", entityNotFoundException.getMessage());
        verify(userRepository, times(1)).findById(TEST_ID);
        verify(itemRepository, times(1)).findByIdFull(TEST_ID);
        verify(bookingRepository, times(0))
                .findByItem_IdAndBooker_IdOrderByStartDesc(TEST_ID, TEST_ID);
        verify(commentRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("addComment item не существует")
    void addCommentItemNotFound() {
        Booking booking = Booking.builder()
                .id(1L)
                .booker(user)
                .status(BookingStatus.REJECTED)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .build();

        when(userRepository.findById(TEST_ID)).thenReturn(Optional.of(user));
        when(userRepository.findById(TEST_ID)).thenReturn(Optional.of(user));
        when(itemRepository.findByIdFull(TEST_ID)).thenReturn(Optional.of(item));
        when(bookingRepository.findByItem_IdAndBooker_IdOrderByStartDesc(TEST_ID, TEST_ID))
                .thenReturn(List.of(booking));

        ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class,
                () -> itemService.addComment(TEST_ID, TEST_ID, CommentRequestDto.builder().text("test").build()));

        assertEquals("400 BAD_REQUEST \"Вещь не найдена у Юзера\"", responseStatusException.getMessage());
        verify(userRepository, times(1)).findById(TEST_ID);
        verify(itemRepository, times(1)).findByIdFull(TEST_ID);
        verify(bookingRepository, times(1))
                .findByItem_IdAndBooker_IdOrderByStartDesc(TEST_ID, TEST_ID);
        verify(commentRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("findByText пустой лист 200 код когда текс=null")
    void findByText_whenTextIsNull_thenReturnEmptyList() {

        String text = null;

        List<ItemDto> result = itemService.findByText(text);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("findByText пустой лист 200 код когда текс=blank")
    void findByText_whenTextIsBlank_thenReturnEmptyList() {

        String text = "   ";

        List<ItemDto> result = itemService.findByText(text);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findAllItemByUserId() {
        Long userId = 1L;
        user.setId(userId);
        Item item = new Item(1L, "Item 1", "Description 1", true, user, 2L);
        item.setId(1L);

        List<Item> itemList = Collections.singletonList(item);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findAllByOwnerIdOrderByIdAsc(user.getId())).thenReturn(itemList);
        when(bookingRepository.findFirstByItem_idAndEndBeforeOrderByEndDesc(anyLong(), any(LocalDateTime.class)))
                .thenReturn(null);
        when(bookingRepository.findFirstByItem_idAndStartAfterOrderByStartAsc(anyLong(), any(LocalDateTime.class)))
                .thenReturn(null);
        when(commentRepository.getCommentsByItem_idOrderByCreatedDesc(anyLong())).thenReturn(Collections.emptyList());

        List<ItemResponseDto> result = itemService.findAllItemByUserId(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        ItemResponseDto responseDto = result.get(0);
        assertEquals(item.getId(), responseDto.getId());
        assertEquals(item.getName(), responseDto.getName());
        assertEquals(item.getDescription(), responseDto.getDescription());

        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1)).findAllByOwnerIdOrderByIdAsc(user.getId());
        verify(bookingRepository, times(1))
                .findFirstByItem_idAndEndBeforeOrderByEndDesc(eq(item.getId()), any(LocalDateTime.class));
        verify(bookingRepository, times(1))
                .findFirstByItem_idAndStartAfterOrderByStartAsc(eq(item.getId()), any(LocalDateTime.class));
        verify(commentRepository, times(1)).getCommentsByItem_idOrderByCreatedDesc(eq(item.getId()));
    }
}