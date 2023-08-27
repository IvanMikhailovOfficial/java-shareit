package ru.practicum.shareit.booking.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
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

    @Query(value = "SELECT bok FROM Booking bok " +
            "JOIN FETCH bok.item it " +
            "JOIN FETCH it.owner " +
            "WHERE bok.booker.id = :bookerId " +
            "ORDER BY bok.start DESC",
            countQuery = "SELECT COUNT(bok) from Booking bok " +
                    "JOIN bok.item it " +
                    "JOIN it.owner ow " +
                    "WHERE bok.booker.id = :bookerId")
    Page<Booking> findByBookerIdOrderByStartDesc(Long bookerId, Pageable pageable);

    @Query(value = "SELECT bok FROM Booking bok " +
            "JOIN FETCH bok.item it " +
            "JOIN FETCH it.owner " +
            "WHERE bok.booker.id = :bookerId " +
            "AND bok.status = :status " +
            "ORDER BY bok.start DESC",
            countQuery = "SELECT COUNT(bok) FROM Booking bok " +
                    "JOIN bok.item it " +
                    "JOIN it.owner " +
                    "WHERE bok.booker.id = :bookerId " +
                    "AND bok.status = :status")
    Page<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status, Pageable pageable);

    @Query(value = "SELECT bok FROM Booking bok " +
            "JOIN FETCH bok.item it " +
            "JOIN FETCH it.owner " +
            "WHERE it.owner.id = :ownerId " +
            "ORDER BY bok.start DESC",
            countQuery = "SELECT COUNT(bok) from Booking bok " +
                    "JOIN bok.item it " +
                    "JOIN it.owner " +
                    "WHERE it.owner.id = :ownerId")
    Page<Booking> findByItemOwnerOrderByStartDesc(Long ownerId, Pageable pageable);

    @Query(value = "SELECT bok FROM Booking bok " +
            "JOIN FETCH bok.item it " +
            "JOIN FETCH it.owner " +
            "WHERE it.owner.id = :ownerId " +
            "AND bok.status = :status " +
            "ORDER BY bok.start DESC",
            countQuery = "SELECT COUNT(bok) FROM Booking bok " +
                    "JOIN bok.item it " +
                    "JOIN it.owner " +
                    "where it.owner.id = :ownerId " +
                    "AND bok.status = :status")
    Page<Booking> findByItemOwnerAndStatusOrderByStartDesc(Long ownerId, BookingStatus status, Pageable pageable);

    @Query(value = "SELECT bok FROM Booking bok " +
            "JOIN FETCH bok.item it " +
            "JOIN FETCH it.owner " +
            "WHERE bok.booker.id = :bookerId " +
            "AND bok.end < :now " +
            "ORDER BY bok.start DESC",
            countQuery = "SELECT COUNT(bok) FROM Booking bok " +
                    "JOIN bok.item it " +
                    "JOIN it.owner " +
                    "WHERE bok.booker.id = :bookerId " +
                    "AND bok.end < :now")
    Page<Booking> findByBookerIdAndEndLessThanOrderByStartDesc(Long bookerId, LocalDateTime now, Pageable pageable);

    @Query(value = "SELECT bok FROM Booking bok " +
            "JOIN FETCH bok.item it " +
            "JOIN FETCH it.owner " +
            "WHERE bok.booker.id = :bookerId " +
            "AND bok.start > :now " +
            "ORDER BY bok.start DESC",
            countQuery = "SELECT COUNT(bok) FROM Booking bok " +
                    "JOIN bok.item it " +
                    "JOIN it.owner " +
                    "WHERE bok.booker.id = :bookerId " +
                    "AND bok.end < :now")
    Page<Booking> findByBookerIdAndStartGreaterThanOrderByStartDesc(Long bookerId, LocalDateTime now, Pageable pageable);

    @Query(value = "SELECT bok FROM Booking bok " +
            "JOIN FETCH bok.item it " +
            "JOIN FETCH it.owner " +
            "WHERE it.owner.id = :ownerId " +
            "AND bok.end < :now " +
            "ORDER BY bok.start DESC",
            countQuery = "SELECT COUNT(bok) FROM Booking bok " +
                    "JOIN bok.item it " +
                    "JOIN it.owner " +
                    "WHERE it.owner.id =: ownerId " +
                    "AND bok.end < :now")
    Page<Booking> findByItemOwnerAndEndLessThanOrderByStartDesc(Long ownerId, LocalDateTime now, Pageable pageable);

    @Query(value = "SELECT bok FROM Booking bok " +
            "JOIN FETCH bok.item it " +
            "JOIN FETCH it.owner " +
            "WHERE it.owner.id = :ownerId " +
            "AND bok.start > :now " +
            "ORDER BY bok.start DESC",
            countQuery = "SELECT COUNT(bok) from Booking bok " +
                    "JOIN bok.item it " +
                    "JOIN it.owner " +
                    "WHERE it.owner.id = :ownerID " +
                    "AND bok.start > :now")
    Page<Booking> findByItemOwnerAndStartGreaterThanOrderByStartDesc(Long ownerId, LocalDateTime now, Pageable pageable);

    @Query(value = "SELECT bok FROM Booking bok " +
            "JOIN FETCH bok.item it " +
            "JOIN FETCH it.owner " +
            "WHERE bok.booker.id = :bookerId " +
            "AND bok.start < :now " +
            "AND bok.end > :now1 " +
            "ORDER BY bok.start DESC",
            countQuery = "SELECT COUNT(bok) from Booking bok " +
                    "JOIN bok.item it " +
                    "JOIN it.owner " +
                    "WHERE bok.booker.id = :bookerId " +
                    "AND bok.start < :now " +
                    "AND bok.end > :now1")
    Page<Booking> findByBookerIdAndStartLessThanAndEndGreaterThanOrderByStartDesc(Long bookerId, LocalDateTime now,
                                                                                  LocalDateTime now1, Pageable pageable);

    @Query(value = "SELECT bok FROM Booking bok " +
            "JOIN FETCH bok.item it " +
            "JOIN FETCH it.owner " +
            "WHERE it.owner.id = :ownerId " +
            "AND bok.start < :now " +
            "AND bok.end > :now1 " +
            "ORDER BY bok.start DESC",
            countQuery = "SELECT COUNT(bok) FROM  Booking bok " +
                    "JOIN bok.item it " +
                    "JOIN it.owner " +
                    "WHERE it.owner.id = :ownerId " +
                    "AND bok.start < :now " +
                    "AND bok.end > now1")
    Page<Booking> findByItemOwnerAndStartLessThanAndEndGreaterThanOrderByStartDesc(Long ownerId, LocalDateTime now,
                                                                                   LocalDateTime now1, Pageable pageable);

    Booking findFirstByItem_idAndEndBeforeOrderByEndDesc(Long itemId, LocalDateTime now);

    Booking findFirstByItem_idAndStartAfterOrderByStartAsc(Long itemId, LocalDateTime now);

    List<Booking> findByItem_IdAndBooker_IdOrderByStartDesc(Long itemId, Long userId);

    List<Booking> findTop1BookingByItemIdAndEndIsBeforeAndStatusIs(Long itemId, LocalDateTime now,
                                                                   BookingStatus approved, Sort end);

    List<Booking> findTop1BookingByItemIdAndEndIsAfterAndStatusIs(Long itemId, LocalDateTime now,
                                                                  BookingStatus approved, Sort end);
}