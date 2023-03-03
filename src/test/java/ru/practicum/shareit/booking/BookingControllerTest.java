package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.UtilsForTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.exception.ErrorHandler;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {
    private final String userIdHeader = "X-Sharer-User-Id";

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());
    private MockMvc mockMvc;

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    @BeforeEach
    void setMockMvc() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController)
                .setControllerAdvice(ErrorHandler.class)
                .build();
    }

    @Test
    void shouldPostBooking() throws Exception {
        User user = UtilsForTest.makeUser(1);
        User user2 = UtilsForTest.makeUser(2);

        ItemRequest itemRequest = UtilsForTest.makeItemRequest(1, user);
        Item item1 = UtilsForTest.makeItem(1, true, user, itemRequest);

        Booking booking = UtilsForTest.makeBooking(1, item1, user2, BookingStatus.WAITING);
        booking.setStart(LocalDateTime.now().plusHours(1));
        BookingDto bookingDto = BookingMapper.toBookingDto(booking);


        String json = objectMapper.writeValueAsString(bookingDto);

        when(bookingService.createBooking(anyInt(), any(BookingDto.class))).thenReturn(booking);

        mockMvc.perform(post("/bookings")
                        .header(userIdHeader, user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(booking)));
    }

    @Test
    void shouldReturnListBookingFullPage() throws Exception {
        User user = UtilsForTest.makeUser(1);
        User user2 = UtilsForTest.makeUser(2);

        ItemRequest itemRequest = UtilsForTest.makeItemRequest(1, user);
        Item item1 = UtilsForTest.makeItem(1, true, user, itemRequest);

        Booking booking = UtilsForTest.makeBooking(1, item1, user2, BookingStatus.WAITING);
        booking.setStart(LocalDateTime.now().plusHours(1));

        Pageable page = Pageable.unpaged();
        when(bookingService.getBookingsByBooker(1, "ALL", page)).thenReturn(List.of(booking));

        mockMvc.perform(get("/bookings")
                        .header(userIdHeader, 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(booking))));
    }

    @Test
    void shouldReturnListBookingByOwnerFullPage() throws Exception {
        User user = UtilsForTest.makeUser(1);
        User user2 = UtilsForTest.makeUser(2);

        ItemRequest itemRequest = UtilsForTest.makeItemRequest(1, user);
        Item item1 = UtilsForTest.makeItem(1, true, user, itemRequest);

        Booking booking = UtilsForTest.makeBooking(1, item1, user2, BookingStatus.WAITING);
        booking.setStart(LocalDateTime.now().plusHours(1));

        Pageable page = Pageable.unpaged();
        when(bookingService.getBookingsByOwner(1, "ALL", page)).thenReturn(List.of(booking));

        mockMvc.perform(get("/bookings/owner")
                        .header(userIdHeader, 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(booking))));
    }

    @Test
    void shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/bookings")
                        .header(userIdHeader, 1)
                        .queryParam("from", "-1")
                        .queryParam("size", "0")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBookingById() throws Exception {
        User user = UtilsForTest.makeUser(1);
        User user2 = UtilsForTest.makeUser(2);

        ItemRequest itemRequest = UtilsForTest.makeItemRequest(1, user);
        Item item1 = UtilsForTest.makeItem(1, true, user, itemRequest);

        Booking booking = UtilsForTest.makeBooking(1, item1, user2, BookingStatus.WAITING);
        booking.setStart(LocalDateTime.now().plusHours(1));

        when(bookingService.getBookingById(1, 1)).thenReturn(booking);

        mockMvc.perform(get("/bookings/" + 1)
                        .header(userIdHeader, 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(booking)));
    }

    @Test
    void shouldUpdateBooking() throws Exception {
        User user = UtilsForTest.makeUser(1);
        User user2 = UtilsForTest.makeUser(2);

        ItemRequest itemRequest = UtilsForTest.makeItemRequest(1, user);
        Item item1 = UtilsForTest.makeItem(1, true, user, itemRequest);

        Booking booking = UtilsForTest.makeBooking(1, item1, user2, BookingStatus.APPROVED);
        booking.setStart(LocalDateTime.now().plusHours(1));

        when(bookingService.change(anyInt(), anyInt(), anyBoolean())).thenReturn(booking);

        mockMvc.perform(patch("/bookings/" + booking.getId())
                        .header(userIdHeader, user.getId())
                        .queryParam("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(booking)));
    }
}
