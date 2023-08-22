package ru.practicum.shareit.booking.repositories;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface BookingRepository extends JpaRepository<Booking, Long> {
    void deleteById(Long id);

    @Query("SELECT bok FROM Booking bok " +
            "JOIN FETCH bok.item it " +
            "JOIN FETCH it.owner " +
            "JOIN FETCH bok.booker " +
            "WHERE bok.id = :bookId")
    Optional<Booking> getBookingFull(Long bookId);

    @Query("SELECT bok FROM Booking bok " +
            "JOIN FETCH bok.item it " +
            "JOIN FETCH it.owner " +
            "WHERE bok.booker.id = :bookerId " +
            "ORDER BY bok.start DESC")
    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    @Query("SELECT bok FROM Booking bok " +
            "JOIN FETCH bok.item it " +
            "JOIN FETCH it.owner " +
            "WHERE bok.booker.id = :bookerId " +
            "AND bok.status = :status " +
            "ORDER BY bok.start DESC")
    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, @Param("status") BookingStatus status);

    @Query("SELECT bok FROM Booking bok " +
            "JOIN FETCH bok.item it " +
            "JOIN FETCH it.owner " +
            "WHERE it.owner.id = :ownerId " +
            "ORDER BY bok.start DESC")
    List<Booking> findByItemOwnerOrderByStartDesc(Long ownerId);

    @Query("SELECT bok FROM Booking bok " +
            "JOIN FETCH bok.item it " +
            "JOIN FETCH it.owner " +
            "WHERE it.owner.id = :ownerId " +
            "AND bok.status = :status " +
            "ORDER BY bok.start DESC")
    List<Booking> findByItemOwnerAndStatusOrderByStartDesc(Long ownerId, BookingStatus status);

    @Query("SELECT bok FROM Booking bok " +
            "JOIN FETCH bok.item it " +
            "JOIN FETCH it.owner " +
            "WHERE bok.booker.id = :bookerId " +
            "AND bok.end < :now " +
            "ORDER BY bok.start DESC")
    List<Booking> findByBookerIdAndEndLessThanOrderByStartDesc(Long bookerId, LocalDateTime now);

    @Query("SELECT bok FROM Booking bok " +
            "JOIN FETCH bok.item it " +
            "JOIN FETCH it.owner " +
            "WHERE bok.booker.id = :bookerId " +
            "AND bok.start > :now " +
            "ORDER BY bok.start DESC")
    List<Booking> findByBookerIdAndStartGreaterThanOrderByStartDesc(Long bookerId, LocalDateTime now);

    @Query("SELECT bok FROM Booking bok " +
            "JOIN FETCH bok.item it " +
            "JOIN FETCH it.owner " +
            "WHERE it.owner.id = :ownerId " +
            "AND bok.end < :now " +
            "ORDER BY bok.start DESC")
    List<Booking> findByItemOwnerAndEndLessThanOrderByStartDesc(Long ownerId, LocalDateTime now);

    @Query("SELECT bok FROM Booking bok " +
            "JOIN FETCH bok.item it " +
            "JOIN FETCH it.owner " +
            "WHERE it.owner.id = :ownerId " +
            "AND bok.start > :now " +
            "ORDER BY bok.start DESC")
    List<Booking> findByItemOwnerAndStartGreaterThanOrderByStartDesc(Long ownerId, LocalDateTime now);

    @Query("SELECT bok FROM Booking bok " +
            "JOIN FETCH bok.item it " +
            "JOIN FETCH it.owner " +
            "WHERE bok.booker.id = :bookerId " +
            "AND bok.start < :now " +
            "AND bok.end > :now1 " +
            "ORDER BY bok.start DESC")
    List<Booking> findByBookerIdAndStartLessThanAndEndGreaterThanOrderByStartDesc(Long bookerId, LocalDateTime now,
                                                                                  LocalDateTime now1);

    @Query("SELECT bok FROM Booking bok " +
            "JOIN FETCH bok.item it " +
            "JOIN FETCH it.owner " +
            "WHERE it.owner.id = :ownerId " +
            "AND bok.start < :now " +
            "AND bok.end > :now1 " +
            "ORDER BY bok.start DESC")
    List<Booking> findByItemOwnerAndStartLessThanAndEndGreaterThanOrderByStartDesc(Long ownerId, LocalDateTime now,
                                                                                   LocalDateTime now1);

    Booking findFirstByItem_idAndEndBeforeOrderByEndDesc(Long itemId, LocalDateTime now);

    Booking findFirstByItem_idAndStartAfterOrderByStartAsc(Long itemId, LocalDateTime now);

    List<Booking> findByItem_IdAndBooker_IdOrderByStartDesc(Long itemId, Long userId);

    List<Booking> findTop1BookingByItemIdAndEndIsBeforeAndStatusIs(Long itemId, LocalDateTime now,
                                                                   BookingStatus approved, Sort end);

    List<Booking> findTop1BookingByItemIdAndEndIsAfterAndStatusIs(Long itemId, LocalDateTime now,
                                                                  BookingStatus approved, Sort end);
}