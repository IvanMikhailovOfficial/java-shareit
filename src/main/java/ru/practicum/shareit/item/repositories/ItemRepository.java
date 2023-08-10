package ru.practicum.shareit.item.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("SELECT it FROM Item it " +
            "JOIN FETCH it.owner  " +
            "WHERE it.available = true" +
            " AND (LOWER(it.name) LIKE LOWER(CONCAT('%', ?1, '%'))" +
            " OR LOWER(it.description) LIKE LOWER(CONCAT('%', ?1, '%')))")
    List<Item> search(String text);

    @Query("SELECT it FROM Item it " +
            "JOIN FETCH it.owner " +
            "WHERE it.id = :itemId")
    Optional<Item> findByIdFull(Long itemId);

    List<Item> findAllByOwnerIdOrderByIdAsc(Long id);
}
