package ru.practicum.shareit.itemRequest.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.exceptions.exp.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repositories.ItemRepository;
import ru.practicum.shareit.itemRequest.dto.ItemRequestInputDto;
import ru.practicum.shareit.itemRequest.dto.ItemRequestOutputDto;
import ru.practicum.shareit.itemRequest.model.ItemRequest;
import ru.practicum.shareit.itemRequest.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repositories.UserRepository;
import ru.practicum.shareit.utility.Utility;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    public static final long ID = 1L;
    private ItemRequestService itemRequestService;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    private ItemRequestOutputDto itemRequestDto;
    private ItemRequest itemRequest;

    private ItemRequestInputDto itemRequestPost;


    @BeforeEach
    void init() {
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, itemRepository, userRepository);
        itemRequestDto = ItemRequestOutputDto.builder()
                .items(List.of())
                .id(ID)
                .description("1")
                .created(LocalDateTime.now())
                .build();
        itemRequestPost = ItemRequestInputDto.builder()
                .description("1")
                .build();

        itemRequest = ItemRequest.builder()
                .id(ID)
                .description("1")
                .created(LocalDateTime.now())
                .requestorId(1L)
                .build();
    }

    @Test
    @DisplayName("getItemRequest 200 status")
    void getItemRequests_whenInvoked_thenListHaveOneRequest() {
        when(userRepository.existsById(eq(ID))).thenReturn(true);
        when(itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(eq(ID)))
                .thenReturn(List.of(itemRequest));
        when(itemRepository.findAllByRequestId(eq(ID))).thenReturn(List.of());


        List<ItemRequestOutputDto> itemRequests = itemRequestService.getItemRequest(ID);

        assertEquals(1, itemRequests.size());
        verify(userRepository, times(1)).existsById(ID);
        verify(itemRequestRepository, times(1))
                .findAllByRequestorIdOrderByCreatedDesc(ID);
    }

    @Test
    @DisplayName("getItemRequest 200 status пустой list")
    void getItemRequests_whenInvokedEmpty_thenListEmpty() {
        when(userRepository.existsById(eq(ID))).thenReturn(true);
        when(itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(eq(ID)))
                .thenReturn(List.of());


        List<ItemRequestOutputDto> itemRequests = itemRequestService.getItemRequest(ID);

        assertEquals(0, itemRequests.size());
        verify(userRepository, times(1)).existsById(ID);
        verify(itemRequestRepository, times(1))
                .findAllByRequestorIdOrderByCreatedDesc(ID);
    }

    @Test
    @DisplayName("getItemRequest user не существует")
    void getItemRequests_whenUserNotExist_thenThrowUserNotFoundException() {
        when(userRepository.existsById(eq(ID))).thenReturn(false);


        ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class,
                () -> itemRequestService.getItemRequest(ID));

        assertEquals("404 NOT_FOUND \"Юзер с id 1 не найден\"", responseStatusException.getMessage());
        verify(userRepository, times(1)).existsById(ID);
        verify(itemRequestRepository, never())
                .findAllByRequestorIdOrderByCreatedDesc(ID);
    }

    @Test
    @DisplayName("getItemRequestsByOtherUsers 200 status")
    void getAllItemRequests_whenInvoked_thenHaveListOneElement() {
        when(userRepository.existsById(ID)).thenReturn(true);
        Pageable pageable1 = PageRequest.of(0, 2, Sort.unsorted());
        when(itemRequestRepository.findAllByRequestorIdNot(ID, pageable1))
                .thenReturn(new PageImpl<>(List.of(itemRequest)));

        List<ItemRequestOutputDto> itemRequests = itemRequestService.getItemRequestsByOtherUsers(ID, 0, 2);

        assertEquals(1, itemRequests.size());
        verify(userRepository, times(1)).existsById(ID);
        verify(itemRequestRepository, times(1))
                .findAllByRequestorIdNot(ID, pageable1);
    }

    @Test
    @DisplayName("getItemRequestsByOtherUsers 200 status пустой list")
    void getAllItemRequests_whenInvoked_thenListEmpty() {
        when(userRepository.existsById(ID)).thenReturn(true);
        Pageable pageable1 = PageRequest.of(0, 2, Sort.unsorted());
        when(itemRequestRepository.findAllByRequestorIdNot(ID, pageable1))
                .thenReturn(new PageImpl<>(List.of()));

        List<ItemRequestOutputDto> itemRequests = itemRequestService.getItemRequestsByOtherUsers(ID, 0, 2);

        assertEquals(0, itemRequests.size());
        verify(userRepository, times(1)).existsById(ID);
        verify(itemRequestRepository, times(1))
                .findAllByRequestorIdNot(ID, pageable1);
    }

    @Test
    @DisplayName("createItemRequest 200 status")
    void createItemRequestTest() {
        when(userRepository.existsById(ID)).thenReturn(true);
        when(itemRequestRepository.save(any())).thenReturn(itemRequest);

        ItemRequestOutputDto requestSave = itemRequestService.createItemRequest(ID, itemRequestPost);

        assertEquals(itemRequestDto.getId(), requestSave.getId());
        assertEquals(itemRequestDto.getCreated().getSecond(), requestSave.getCreated().getSecond());
        assertEquals(itemRequestDto.getDescription(), requestSave.getDescription());
        verify(userRepository, times(1)).existsById(ID);
        verify(itemRequestRepository, times(1)).save(any(ItemRequest.class));
    }

    @Test
    @DisplayName("getItemRequestsByIdOwner успешное получение")
    void getItemRequestsByIdOwner_whenValidUserIdAndItemRequestId_thenReturnsItemRequestOutputDto() {
        Long userId = 1L;
        Long itemRequestId = 2L;
        User user = new User();
        user.setId(userId);
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(itemRequestId);
        itemRequest.setRequestorId(user.getId());
        Item item1 = new Item();
        item1.setName("item1");
        item1.setDescription("description1");
        Item item2 = new Item();
        item2.setName("item2");
        item2.setDescription("description2");
        List<Item> items = List.of(item1, item2);


        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRequestRepository.existsById(itemRequestId)).thenReturn(true);
        when(itemRequestRepository.findById(itemRequestId)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findAllByRequestId(itemRequestId)).thenReturn(items);

        ItemRequestOutputDto result = itemRequestService.getItemRequestsByIdOwner(userId, itemRequestId);

        assertNotNull(result);
        assertEquals(itemRequestId, result.getId());

        assertEquals(items.size(), result.getItems().size());
        assertEquals(items.get(0).getName(), result.getItems().get(0).getName());
        assertEquals(items.get(0).getDescription(), result.getItems().get(0).getDescription());
        assertEquals(items.get(1).getName(), result.getItems().get(1).getName());
        assertEquals(items.get(1).getDescription(), result.getItems().get(1).getDescription());

        verify(userRepository, times(1)).existsById(userId);
        verify(itemRequestRepository, times(1)).existsById(itemRequestId);
        verify(itemRequestRepository, times(1)).findById(itemRequestId);
        verify(itemRepository, times(1)).findAllByRequestId(itemRequestId);
    }

    @Test
    @DisplayName("getItemRequestsByIdOwner неверный user")
    void getItemRequestsByIdOwner_whenInvalidUserId_thenThrowsEntityNotFoundException() {
        Long userId = 1L;
        Long itemRequestId = 2L;

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> itemRequestService.getItemRequestsByIdOwner(userId, itemRequestId));

        verify(userRepository, times(1)).existsById(userId);
        verify(itemRequestRepository, times(0)).existsById(itemRequestId);
        verify(itemRequestRepository, times(0)).findById(itemRequestId);
        verify(itemRepository, times(0)).findAllByRequestId(itemRequestId);
    }

    @Test
    @DisplayName("getItemRequestsByIdOwner неверный itemrequest id")
    void getItemRequestsByIdOwner_whenInvalidItemRequestId_thenThrowsResponseStatusException() {
        Long userId = 1L;
        Long itemRequestId = 2L;

        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRequestRepository.existsById(itemRequestId)).thenReturn(false);


        assertThrows(ResponseStatusException.class, () -> itemRequestService.getItemRequestsByIdOwner(userId, itemRequestId));

        verify(userRepository, times(1)).existsById(userId);
        verify(itemRequestRepository, times(1)).existsById(itemRequestId);
        verify(itemRequestRepository, times(0)).findById(itemRequestId);
        verify(itemRepository, times(0)).findAllByRequestId(itemRequestId);
    }

    @Test
    @DisplayName("getItemRequestsByOtherUsers неверный userId")
    void getItemRequestsByOtherUsers_whenInvalidUserId_thenThrowsEntityNotFoundException() {
        Long userId = 1L;
        Integer from = 0;
        Integer size = 10;

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> itemRequestService.getItemRequestsByOtherUsers(userId, from, size));

        verify(userRepository, times(1)).existsById(userId);
        verify(itemRequestRepository, times(0)).findAllByRequestorIdNot(userId,
                Utility.paginationWithoutSort(from, size));
        verify(itemRepository, times(0)).findAllByRequestId(anyLong());
    }

    @Test
    @DisplayName("createItemRequest неверный user id")
    void createItemRequest_whenInvalidUserId_thenThrowsEntityNotFoundException() {

        Long userId = 1L;
        ItemRequestInputDto itemRequestInputDto = new ItemRequestInputDto();

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> itemRequestService.createItemRequest(userId, itemRequestInputDto));

        verify(userRepository, times(1)).existsById(userId);
        verify(itemRequestRepository, times(0)).save(any(ItemRequest.class));
    }
}