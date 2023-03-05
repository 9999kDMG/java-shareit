package ru.practicum.shareit;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

public class UtilsForTest {
    public static User makeUser(int id) {
        return User.builder()
                .id(id)
                .name("user")
                .email("test@email.org").build();
    }

    public static ItemRequest makeItemRequest(int id, User user) {
        return ItemRequest.builder()
                .id(id)
                .created(LocalDateTime.now())
                .description("about itemRequest")
                .user(user).build();
    }

    public static Item makeItem(int id, boolean available, User user, ItemRequest request) {
        return Item.builder()
                .id(id)
                .name("item")
                .description("about item")
                .available(available)
                .owner(user)
                .request(request)
                .build();
    }

    public static ItemDto makeItemDto(int id, boolean available, int request) {
        return ItemDto.builder()
                .id(id)
                .name("item")
                .description("about item")
                .available(available)
                .requestId(request)
                .build();
    }

    public static Booking makeBooking(int id, Item item, User booker, BookingStatus bookingstatus) {
        return Booking.builder()
                .id(id)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(5))
                .item(item)
                .booker(booker)
                .status(bookingstatus)
                .build();
    }

    public static Comment makeComment(int id, Item item, User user) {
        return Comment.builder()
                .id(id)
                .text("text of comment for test")
                .item(item)
                .author(user)
                .created(LocalDateTime.now())
                .build();
    }
}
