package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b FROM Booking b JOIN Item i ON b.item.id = i.id " +
            "WHERE b.booker.id = :bookerId AND i.id = :itemId AND b.status = 'APPROVED' AND b.end < :currentTime")
    List<Booking> getAllUserBookings(Long bookerId, Long itemId, LocalDateTime currentTime);

    @Query("SELECT b FROM Booking b JOIN Item i ON b.item.id = i.id " +
            "WHERE b.booker.id = :bookerId " +
            "ORDER BY b.start DESC")
    List<Booking> getAllBookingsByBookerId(Long bookerId);

    @Query("SELECT b FROM Booking b JOIN Item i ON b.item.id = i.id " +
            "WHERE b.booker.id = :bookerId AND :currentTime BETWEEN b.start AND b.end " +
            "ORDER BY b.start DESC")
    List<Booking> getAllCurrentBookingsByBookerId(Long bookerId, LocalDateTime currentTime);

    @Query("SELECT b FROM Booking b JOIN Item i ON b.item.id = i.id " +
            "WHERE b.booker.id = :bookerId AND b.start > :currentTime " +
            "ORDER BY b.start DESC")
    List<Booking> getAllFutureBookingsByBookerId(Long bookerId, LocalDateTime currentTime);

    @Query("SELECT b FROM Booking b JOIN Item i ON b.item.id = i.id " +
            "WHERE b.booker.id = :bookerId AND b.status = 'REJECTED' " +
            "ORDER BY b.start DESC")
    List<Booking> getAllRejectedBookingsByBookerId(Long bookerId);

    @Query("SELECT b FROM Booking b JOIN Item i ON b.item.id = i.id " +
            "WHERE b.booker.id = :bookerId AND b.end < :currentTime " +
            "ORDER BY b.start DESC")
    List<Booking> getAllPastBookingsByBookerId(Long bookerId, LocalDateTime currentTime);

    @Query("SELECT b FROM Booking b JOIN Item i ON b.item.id = i.id " +
            "WHERE b.booker.id = :bookerId AND b.status = 'WAITING' AND b.start > :currentTime " +
            "ORDER BY b.start DESC")
    List<Booking> getAllWaitingBookingsByBookerId(Long bookerId, LocalDateTime currentTime);

    @Query("SELECT b FROM Booking b JOIN Item i ON b.item.id = i.id " +
            "WHERE i.owner.id = :ownerId " +
            "ORDER BY b.start DESC")
    List<Booking> getAllBookingsByOwnerId(Long ownerId);

    @Query("SELECT b FROM Booking b JOIN Item i ON b.item.id = i.id " +
            "WHERE i.owner.id = :ownerId AND :currentTime BETWEEN b.start AND b.end " +
            "ORDER BY b.start DESC")
    List<Booking> getAllCurrentBookingsByOwnerId(Long ownerId, LocalDateTime currentTime);

    @Query("SELECT b FROM Booking b JOIN Item i ON b.item.id = i.id " +
            "WHERE i.owner.id = :ownerId AND b.status = 'WAITING' AND b.start > :currentTime " +
            "ORDER BY b.start DESC")
    List<Booking> getAllWaitingBookingsByOwnerId(Long ownerId, LocalDateTime currentTime);

    @Query("SELECT b FROM Booking b JOIN Item i ON b.item.id = i.id " +
            "WHERE i.owner.id = :ownerId AND b.start > :currentTime " +
            "ORDER BY b.start DESC")
    List<Booking> getAllFutureBookingsByOwnerId(Long ownerId, LocalDateTime currentTime);

    @Query("SELECT b FROM Booking b JOIN Item i ON b.item.id = i.id " +
            "WHERE i.owner.id = :ownerId AND b.status = 'REJECTED' " +
            "ORDER BY b.start DESC")
    List<Booking> getAllRejectedBookingsByOwnerId(Long ownerId);

    @Query("SELECT b FROM Booking b JOIN Item i ON b.item.id = i.id " +
            "WHERE i.owner.id = :ownerId AND b.end < :currentTime " +
            "ORDER BY b.start DESC")
    List<Booking> getAllPastBookingsByOwnerId(Long ownerId, LocalDateTime currentTime);

   // для получения последнего и первого бронирования конкретной вещи
    Optional<Booking> findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(Long itemId, Status status, LocalDateTime now);

    Optional<Booking> findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(Long itemId, Status status, LocalDateTime now);
}
