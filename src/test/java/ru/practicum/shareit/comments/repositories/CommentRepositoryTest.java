package ru.practicum.shareit.comments.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.comments.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CommentRepositoryTest {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void init() {
        User owner = User.builder()
                .name("1")
                .email("1@mail.com")
                .build();

        User userPersist = entityManager.persist(owner);

        Item item = Item.builder()
                .name("1 1")
                .description("1 1")
                .available(true)
                .owner(userPersist)
                .build();

        Item itemPersist = entityManager.persist(item);

        Comment comment = Comment.builder()
                .created(LocalDateTime.now())
                .author(userPersist)
                .item(itemPersist)
                .text("1")
                .build();

        entityManager.persist(comment);
    }

    @Test
    void getCommentsByItem_idOrderByCreatedDesc_whenInvoked_thenListHaveOneComment() {
        List<Comment> comments = commentRepository.getCommentsByItem_idOrderByCreatedDesc(1L);
        assertEquals(1, comments.size());
    }

    @Test
    void getCommentsByItem_idOrderByCreatedDesc_whenInvokedIdNotExist_thenListEmpty() {
        List<Comment> comments = commentRepository.getCommentsByItem_idOrderByCreatedDesc(0L);
        assertEquals(0, comments.size());
    }
}