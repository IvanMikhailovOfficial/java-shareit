package ru.practicum.shareit.itemRequest.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import ru.practicum.shareit.itemRequest.model.ItemRequest;
import ru.practicum.shareit.itemRequest.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utility.Utility;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemRequestRepositoryTest {
    @Autowired
    ItemRequestRepository itemRequestRepository;
    @Autowired
    TestEntityManager entityManager;

    @BeforeEach
    void init() {
        User owner = User.builder()
                .name("1")
                .email("1@mail.com")
                .build();
        User user = entityManager.persist(owner);

        User owner2 = User.builder()
                .name("1t")
                .email("2@mail.com")
                .build();
        User user1 = entityManager.persist(owner2);

        ItemRequest itemRequest = ItemRequest.builder()
                .requestorId(user.getId())
                .created(LocalDateTime.now())
                .description("test")
                .build();

        entityManager.persist(itemRequest);

        ItemRequest itemRequest1 = ItemRequest.builder()
                .requestorId(user1.getId())
                .created(LocalDateTime.now())
                .description("1")
                .build();

        entityManager.persist(itemRequest1);
    }

    @Test
    void findItemRequestById_whenInvoked_thenTrue() {
        List<ItemRequest> all = itemRequestRepository.findAll();
        Optional<ItemRequest> itemRequestById = itemRequestRepository.findById(all.get(0).getId());
        assertTrue(itemRequestById.isPresent());
    }

    @Test
    void findItemRequestById_whenInvokedNotExistIRequest_thenFalse() {
        assertTrue(itemRequestRepository.findById(0L).isEmpty());
    }

    @Test
    void findAllByRequestorIdOrderByCreatedDesc_whenInvoked_thenHaveListOneRequest() {
        List<ItemRequest> allByRequestorIdOrderByCreatedDesc = itemRequestRepository
                .findAllByRequestorIdOrderByCreatedDesc(1L);
        assertEquals(1, allByRequestorIdOrderByCreatedDesc.size());
    }

    @Test
    void findAllByRequestorIdOrderByCreatedDesc_whenInvoked_thenHaveEmptyList() {
        List<ItemRequest> allByRequestorIdOrderByCreatedDesc = itemRequestRepository
                .findAllByRequestorIdOrderByCreatedDesc(0L);
        assertEquals(0, allByRequestorIdOrderByCreatedDesc.size());
    }

    @Test
    void findAllByRequestorIdNot_whenInvoked_thenDontHaveIdRequestor() {
        List<ItemRequest> all = itemRequestRepository.findAll();
        List<ItemRequest> collect = itemRequestRepository
                .findAllByRequestorIdNot(all.get(0).getId(), Utility.paginationWithoutSort(0, 1))
                .stream().collect(Collectors.toList());

        assertNotSame(collect.get(0).getId(), all.get(0).getId());
    }
}