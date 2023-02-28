package ru.practicum.shareit.booking;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.UtilsForTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.exception.BadRequestException;
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

    @InjectMocks
    BookingService bookingService;

    @Test
    void shouldCreateBooking() {
        User user = UtilsForTest.makeUser(1);

        ItemRequest itemRequest = UtilsForTest.makeItemRequest(1, user);
        Item item1 = UtilsForTest.makeItem(1, true, user, itemRequest);

        Booking booking = UtilsForTest.makeBooking(1, item1, user, null);
        booking.setStart(LocalDateTime.now().plusHours(1));
        BookingDto bookingDto = BookingMapper.toBookingDto(booking);

        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.ofNullable(item1));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        Assertions.assertThat(bookingService.createBooking(2, bookingDto)).isEqualTo(booking);
    }

    @Test
    void shouldGetBookingById() {
        User user = UtilsForTest.makeUser(1);

        ItemRequest itemRequest = UtilsForTest.makeItemRequest(1, user);
        Item item1 = UtilsForTest.makeItem(1, true, user, itemRequest);

        Booking booking = UtilsForTest.makeBooking(5, item1, user, null);
        booking.setStart(LocalDateTime.now().plusHours(1));

        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));
        Assertions.assertThat(bookingService.getBookingById(1, 5)).isEqualTo(booking);
    }

    @Test
    void shouldGetBookingsByOwner() {
        User user = UtilsForTest.makeUser(1);

        ItemRequest itemRequest = UtilsForTest.makeItemRequest(1, user);
        Item item1 = UtilsForTest.makeItem(1, true, user, itemRequest);

        Booking booking = UtilsForTest.makeBooking(5, item1, user, BookingStatus.APPROVED);
        booking.setStart(LocalDateTime.now().plusHours(1));

        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(anyInt(), any(Pageable.class)))
                .thenReturn(List.of(booking));
        Assertions.assertThat(bookingService.getBookingsByOwner(1, "ALL", Pageable.unpaged()))
                .isEqualTo(List.of(booking));
    }

    @Test
    void shouldGetBookingsByBooker() {
        User user = UtilsForTest.makeUser(1);

        ItemRequest itemRequest = UtilsForTest.makeItemRequest(1, user);
        Item item1 = UtilsForTest.makeItem(1, true, user, itemRequest);

        Booking booking = UtilsForTest.makeBooking(5, item1, user, BookingStatus.APPROVED);
        booking.setStart(LocalDateTime.now().plusHours(1));

        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(anyInt(), any(Pageable.class)))
                .thenReturn(List.of(booking));
        Assertions.assertThat(bookingService.getBookingsByBooker(1, "ALL", Pageable.unpaged()))
                .isEqualTo(List.of(booking));
    }

    @Test
    void shouldApproveAndReturnBooking() {
        User user = UtilsForTest.makeUser(1);

        ItemRequest itemRequest = UtilsForTest.makeItemRequest(1, user);
        Item item1 = UtilsForTest.makeItem(1, true, user, itemRequest);

        Booking booking = UtilsForTest.makeBooking(1, item1, user, BookingStatus.APPROVED);
        booking.setStart(LocalDateTime.now().plusHours(1));

        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));
        Assertions.assertThatThrownBy(() -> bookingService.change(1, 1, true)).isInstanceOf(BadRequestException.class);
    }
}
