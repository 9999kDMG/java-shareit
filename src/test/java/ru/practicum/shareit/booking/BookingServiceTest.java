package ru.practicum.shareit.booking;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.UtilsForTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.exception.BadRequestException;
import ru.practicum.shareit.item.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;

    private User user;
    private ItemRequest itemRequest;
    private Item item1;
    private Booking booking;
    private BookingDto bookingDto;
    @InjectMocks
    BookingService bookingService;

    @BeforeEach
    void createEntityForTest() {
        user = UtilsForTest.makeUser(1);

        itemRequest = UtilsForTest.makeItemRequest(1, user);
        item1 = UtilsForTest.makeItem(1, true, user, itemRequest);

        booking = UtilsForTest.makeBooking(1, item1, user, null);
        booking.setStart(LocalDateTime.now().plusHours(1));
        bookingDto = BookingMapper.toBookingDto(booking);

    }

    @Test
    void shouldCreateBooking() {

        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.ofNullable(item1));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        Assertions.assertThat(bookingService.createBooking(2, bookingDto)).isEqualTo(booking);
    }

    @Test
    void shouldGetBookingById() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));
        Assertions.assertThat(bookingService.getBookingById(1, 5)).isEqualTo(booking);
    }

    @Test
    void shouldGetBookingsByOwner() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(anyInt(), any(Pageable.class)))
                .thenReturn(List.of(booking));
        Assertions.assertThat(bookingService.getBookingsByOwner(1, "ALL", Pageable.unpaged()))
                .isEqualTo(List.of(booking));
    }

    @Test
    void shouldGetBookingsByOwnerStatusCurrent() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository
                .findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        anyInt(),
                        any(LocalDateTime.class),
                        any(LocalDateTime.class),
                        any(Pageable.class)))
                .thenReturn(List.of(booking));
        Assertions.assertThat(bookingService.getBookingsByOwner(1, "CURRENT", Pageable.unpaged()))
                .isEqualTo(List.of(booking));
    }

    @Test
    void shouldGetBookingsByOwnerStatusPast() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository
                .findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(
                        anyInt(),
                        any(LocalDateTime.class),
                        any(Pageable.class)))
                .thenReturn(List.of(booking));
        Assertions.assertThat(bookingService.getBookingsByOwner(1, "PAST", Pageable.unpaged()))
                .isEqualTo(List.of(booking));
    }

    @Test
    void shouldGetBookingsByOwnerStatusFuture() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository
                .findAllByItemOwnerIdAndStartAfterOrderByStartDesc(
                        anyInt(),
                        any(LocalDateTime.class),
                        any(Pageable.class)))
                .thenReturn(List.of(booking));
        Assertions.assertThat(bookingService.getBookingsByOwner(1, "FUTURE", Pageable.unpaged()))
                .isEqualTo(List.of(booking));
    }

    @Test
    void shouldGetBookingsByOwnerStatusWaiting() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository
                .findAllByItemOwnerIdAndStatusOrderByStartDesc(
                        anyInt(),
                        any(BookingStatus.class),
                        any(Pageable.class)))
                .thenReturn(List.of(booking));
        Assertions.assertThat(bookingService.getBookingsByOwner(1, "WAITING", Pageable.unpaged()))
                .isEqualTo(List.of(booking));
    }

    @Test
    void shouldGetBookingsByOwnerStatusRejected() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository
                .findAllByItemOwnerIdAndStatusOrderByStartDesc(
                        anyInt(),
                        any(BookingStatus.class),
                        any(Pageable.class)))
                .thenReturn(List.of(booking));
        Assertions.assertThat(bookingService.getBookingsByOwner(1, "REJECTED", Pageable.unpaged()))
                .isEqualTo(List.of(booking));
    }

    @Test
    void shouldThrowBadRequestExceptionByUnknownStatusBookingOfOwner() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        Assertions.assertThatThrownBy(
                        () -> bookingService.getBookingsByOwner(1, "popopo", Pageable.unpaged()))
                .isInstanceOf(BadRequestException.class
                );
    }

    @Test
    void shouldGetBookingsByBooker() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(anyInt(), any(Pageable.class)))
                .thenReturn(List.of(booking));
        Assertions.assertThat(bookingService.getBookingsByBooker(1, "ALL", Pageable.unpaged()))
                .isEqualTo(List.of(booking));
    }

    @Test
    void shouldGetBookingsByBookerStatusCurrent() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository
                .findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        anyInt(),
                        any(LocalDateTime.class),
                        any(LocalDateTime.class),
                        any(Pageable.class)))
                .thenReturn(List.of(booking));
        Assertions.assertThat(bookingService.getBookingsByBooker(1, "CURRENT", Pageable.unpaged()))
                .isEqualTo(List.of(booking));
    }

    @Test
    void shouldGetBookingsByBookerStatusPast() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository
                .findAllByBookerIdAndEndBeforeOrderByStartDesc(
                        anyInt(),
                        any(LocalDateTime.class),
                        any(Pageable.class)))
                .thenReturn(List.of(booking));
        Assertions.assertThat(bookingService.getBookingsByBooker(1, "PAST", Pageable.unpaged()))
                .isEqualTo(List.of(booking));
    }

    @Test
    void shouldGetBookingsByBookerStatusFuture() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository
                .findAllByBookerIdAndStartAfterOrderByStartDesc(
                        anyInt(),
                        any(LocalDateTime.class),
                        any(Pageable.class)))
                .thenReturn(List.of(booking));
        Assertions.assertThat(bookingService.getBookingsByBooker(1, "FUTURE", Pageable.unpaged()))
                .isEqualTo(List.of(booking));
    }

    @Test
    void shouldGetBookingsByBookerStatusWaiting() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository
                .findAllByBookerIdAndStatusOrderByStartDesc(
                        anyInt(),
                        any(BookingStatus.class),
                        any(Pageable.class)))
                .thenReturn(List.of(booking));
        Assertions.assertThat(bookingService.getBookingsByBooker(1, "WAITING", Pageable.unpaged()))
                .isEqualTo(List.of(booking));
    }

    @Test
    void shouldGetBookingsByBookerStatusRejected() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository
                .findAllByBookerIdAndStatusOrderByStartDesc(
                        anyInt(),
                        any(BookingStatus.class),
                        any(Pageable.class)))
                .thenReturn(List.of(booking));
        Assertions.assertThat(bookingService.getBookingsByBooker(1, "REJECTED", Pageable.unpaged()))
                .isEqualTo(List.of(booking));
    }

    @Test
    void shouldThrowBadRequestExceptionByUnknownStatus() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        Assertions.assertThatThrownBy(
                        () -> bookingService.getBookingsByBooker(1, "popopo", Pageable.unpaged()))
                .isInstanceOf(BadRequestException.class
                );
    }

    @Test
    void shouldThrowBadRequestException() {
        booking.setStatus(BookingStatus.APPROVED);
        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));
        Assertions.assertThatThrownBy(() -> bookingService.change(1, 1, true)).isInstanceOf(BadRequestException.class);
    }

    @Test
    void shouldThrowNotFoundException() {
        User user2 = UtilsForTest.makeUser(2);
        Item item2 = UtilsForTest.makeItem(1, true, user2, itemRequest);

        booking.setItem(item2);
        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));
        Assertions.assertThatThrownBy(() -> bookingService.change(1, 1, true)).isInstanceOf(NotFoundException.class);
    }
}
