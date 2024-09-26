package ru.practicum.shareit.booking.repo;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBooker_IdAndEndIsBefore(Long bookerId, LocalDateTime end, Sort sort);

    List<Booking> findByBookerIdAndStatus(Long bookerId, BookingStatus status, Sort sort);

    List<Booking> findByOwnerIdAndStatus(Long ownerId, BookingStatus status, Sort sort);

    List<Booking> findByBookerId(Long bookerId, Sort sort);

    List<Booking> findByOwnerId(Long ownerId, Sort sort);

    // Метод для получения последнего бронирования
    Optional<Booking> findFirstByItemIdAndEndBeforeOrderByEndDesc(Long itemId, LocalDateTime currentTime);

    // Метод для получения ближайшего будущего бронирования
    Optional<Booking> findFirstByItemIdAndStartAfterOrderByStartAsc(Long itemId, LocalDateTime currentTime);

    boolean existsByItemIdAndBookerIdAndEndBefore(Long itemId, Long userId, LocalDateTime time);

    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId " +
            "AND (b.start < :end1 AND b.end > :start1)")
    List<Booking> findByItemIdAndStartBetweenOrEndBetween(
            @Param("itemId") Long itemId,
            @Param("start1") LocalDateTime start1,
            @Param("end1") LocalDateTime end1);

    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId " +
            "AND (b.start < :end AND b.end > :start)")
    List<Booking> findOverlappingBookings(
            @Param("itemId") Long itemId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}
