package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.FieldValidationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnsupportedStatusException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public Booking createBooking(int userId, BookingDto bookingDto) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("user id N%s", userId)));
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException(String.format("item id N%s", bookingDto.getItemId())));

        if (item.getOwner().getId() == userId) {
            throw new NotFoundException(String.format("item id N%s", bookingDto.getItemId()));
        }

        if (!item.getAvailable()) {
            throw new FieldValidationException(String.format("Item with item id N%s is unavailable", bookingDto.getItemId()));
        }

        Booking booking = BookingMapper.toBooking(bookingDto);

        if (booking.getStart().isBefore(LocalDateTime.now())
                || booking.getEnd().isBefore(booking.getStart())
                || booking.getEnd().isBefore(LocalDateTime.now())) {
            throw new FieldValidationException(String.format("incorrect booking date item id N%s", bookingDto.getItemId()));
        }

        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        return bookingRepository.save(booking);
    }

    public Booking change(int userId, int id, boolean approved) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("user id N%s", userId)));
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("booking id N%s", id)));

        if (!user.equals(booking.getItem().getOwner())) {
            throw new NotFoundException(String.format("user id N%s have not item", userId));
        }

        if (booking.getStatus() == BookingStatus.APPROVED || booking.getStatus() == BookingStatus.REJECTED) {
            throw new FieldValidationException(String.format("booking id N%s already has a changed status", id));
        }

        BookingStatus newStatus = approved ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        booking.setStatus(newStatus);
        return bookingRepository.save(booking);
    }

    public Booking getBookingById(int userId, int id) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("user id N%s", userId)));

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("booking id N%s", id)));

        if (!(booking.getBooker().equals(user) || booking.getItem().getOwner().equals(user))) {
            throw new NotFoundException(String.format("there is no booking for user id N%s", userId));
        }
        return booking;
    }


    public List<Booking> getBookingsByBooker(int userId, String state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("user id N%s", userId)));

        switch (state) {
            case "ALL":
                return bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
            case "CURRENT":
                return bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now());
            case "PAST":
                return bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
            case "FUTURE":
                return bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
            case "WAITING":
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
            case "REJECTED":
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
            default:
                throw new UnsupportedStatusException();
        }
    }

    public List<Booking> getBookingsByOwner(int userId, String state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("user id N%s", userId)));

        switch (state) {
            case "ALL":
                return bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId);
            case "CURRENT":
                return bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now());
            case "PAST":
                return bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
            case "FUTURE":
                return bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
            case "WAITING":
                return bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
            case "REJECTED":
                return bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
            default:
                throw new UnsupportedStatusException();
        }
    }
}
