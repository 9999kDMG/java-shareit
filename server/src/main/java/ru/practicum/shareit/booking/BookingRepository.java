package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findAllByBookerIdOrderByStartDesc(int userId, Pageable page);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(int userId, LocalDateTime now, LocalDateTime now1, Pageable page);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(int userId, LocalDateTime now, Pageable page);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(int userId, LocalDateTime now, Pageable page);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(int userId, BookingStatus waiting, Pageable page);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(int userId, Pageable page);

    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(int userId, LocalDateTime now, LocalDateTime now1, Pageable page);

    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(int userId, LocalDateTime now, Pageable page);

    List<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(int userId, LocalDateTime now, Pageable page);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(int userId, BookingStatus waiting, Pageable page);

    List<Booking> findAllByItemIdOrderByStart(int id);

    List<Booking> findAllByItemIdAndBookerIdAndEndBeforeOrderByStartDesc(int id, int userId, LocalDateTime now);
}
