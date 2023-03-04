package ru.practicum.shareit.item;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.UtilsForTest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    @Mock
    ItemRepository itemRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    BookingRepository bookingRepository;

    @Mock
    CommentRepository commentRepository;

    @Mock
    ItemRequestRepository itemRequestRepository;

    @InjectMocks
    ItemService itemService;

    @Test
    void shouldCreateItemWORequest() {
        User user = UtilsForTest.makeUser(1);
        ItemDto itemDto1 = UtilsForTest.makeItemDto(1, true, 1);
        ItemRequest itemRequest = UtilsForTest.makeItemRequest(1, user);
        Item item = UtilsForTest.makeItem(1, true, user, itemRequest);

        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        when(itemRequestRepository.findById(anyInt())).thenReturn(Optional.ofNullable(itemRequest));
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        Assertions.assertThat(itemService.createItem(1, itemDto1)).isEqualTo(itemDto1);
    }

    @Test
    void shouldReturnNotFoundUser() {
        ItemDto itemDto1 = UtilsForTest.makeItemDto(1, true, 1);

        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());
        Assertions.assertThatThrownBy(() -> itemService.createItem(1, itemDto1))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldReturnNotFoundItemRequest() {
        User user = UtilsForTest.makeUser(1);

        ItemDto itemDto1 = UtilsForTest.makeItemDto(1, true, 1);
        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        when(itemRequestRepository.findById(anyInt())).thenReturn(Optional.empty());
        Assertions.assertThatThrownBy(() -> itemService.createItem(1, itemDto1))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldReturnItemById() {
        User user = UtilsForTest.makeUser(1);
        ItemRequest itemRequest = UtilsForTest.makeItemRequest(1, user);
        Item item = UtilsForTest.makeItem(1, true, user, itemRequest);
        Booking booking = UtilsForTest.makeBooking(1, item, user, BookingStatus.APPROVED);
        Comment comment = UtilsForTest.makeComment(1, item, user);
        ItemDto itemDto1 = ItemMapper.toItemDto(item);
        itemDto1.setLastBooking(null);
        itemDto1.setNextBooking(BookingMapper.toPartBookingDto(booking));
        itemDto1.setComments(List.of(CommentMapper.toCommentDto(comment)));

        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByItemIdOrderByStart(anyInt())).thenReturn(List.of(booking));
        when(commentRepository.findAllByItemIdOrderByCreatedDesc(anyInt())).thenReturn(List.of(comment));
        Assertions.assertThat(itemService.getItemById(1, 1)).isEqualTo(itemDto1);
    }

    @Test
    void shouldReturnAllItem() {
        User user = UtilsForTest.makeUser(1);
        ItemRequest itemRequest = UtilsForTest.makeItemRequest(1, user);
        Item item1 = UtilsForTest.makeItem(1, true, user, itemRequest);
        Item item2 = UtilsForTest.makeItem(2, true, user, itemRequest);
        List<Item> items = List.of(item1,item2);

        Booking booking = UtilsForTest.makeBooking(1, item1, user, BookingStatus.APPROVED);
        Comment comment = UtilsForTest.makeComment(1, item1, user);

        ItemDto itemDto1 = ItemMapper.toItemDto(item1);
        ItemDto itemDto2 = ItemMapper.toItemDto(item2);
        itemDto1.setLastBooking(null);
        itemDto1.setNextBooking(BookingMapper.toPartBookingDto(booking));
        itemDto1.setComments(List.of(CommentMapper.toCommentDto(comment)));
        List<ItemDto> itemDtos = List.of(itemDto1, itemDto2);
        itemDto2.setLastBooking(null);
        itemDto2.setNextBooking(BookingMapper.toPartBookingDto(booking));
        itemDto2.setComments(List.of(CommentMapper.toCommentDto(comment)));

        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findAllByOwnerId(anyInt(), any(Pageable.class))).thenReturn(items);
        when(bookingRepository.findAllByItemIdOrderByStart(anyInt())).thenReturn(List.of(booking));
        when(commentRepository.findAllByItemIdOrderByCreatedDesc(anyInt())).thenReturn(List.of(comment));
        Assertions.assertThat(itemService.getAllItemsUser(1, Pageable.unpaged())).isEqualTo(itemDtos);
    }

    @Test
    void shouldDeleteUser() {
        User user = UtilsForTest.makeUser(1);
        Item item = UtilsForTest.makeItem(1, true, user, null);
        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        itemService.delete(1, 1);
        verify(itemRepository, Mockito.times(1)).deleteById(1);
    }

    @Test
    void shouldChangeItemAndReturn() {
        User user = UtilsForTest.makeUser(1);
        ItemRequest itemRequest = UtilsForTest.makeItemRequest(1, user);
        Item item1 = UtilsForTest.makeItem(1, true, user, itemRequest);
        Item item2 = UtilsForTest.makeItem(1, true, user, itemRequest);
        item2.setDescription("new description");

        ItemDto itemDto2 = ItemMapper.toItemDto(item2);

        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item1));
        when(itemRepository.save(any(Item.class))).thenReturn(item2);
        Assertions.assertThat(itemService.change(1, 1, itemDto2)).isEqualTo(itemDto2);
    }

    @Test
    void shouldReturnAllItemByText() {
        User user = UtilsForTest.makeUser(1);
        String textForSearch = "search this for me";

        ItemRequest itemRequest = UtilsForTest.makeItemRequest(1, user);
        Item item1 = UtilsForTest.makeItem(1, true, user, itemRequest);
        Item item2 = UtilsForTest.makeItem(2, true, user, itemRequest);
        List<Item> items = List.of(item1,item2);

        ItemDto itemDto1 = ItemMapper.toItemDto(item1);
        ItemDto itemDto2 = ItemMapper.toItemDto(item2);
        List<ItemDto> itemDtos = List.of(itemDto1, itemDto2);

        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findAllByText(textForSearch, Pageable.unpaged())).thenReturn(items);
        Assertions.assertThat(itemService.searchByText(1, textForSearch, Pageable.unpaged()))
                .isEqualTo(itemDtos);
    }

    @Test
    void shouldWriteComment() {
        User user = UtilsForTest.makeUser(1);
        ItemRequest itemRequest = UtilsForTest.makeItemRequest(1, user);
        Item item1 = UtilsForTest.makeItem(1, true, user, itemRequest);

        Booking booking = UtilsForTest.makeBooking(1, item1, user, BookingStatus.APPROVED);
        Comment comment = UtilsForTest.makeComment(1, item1, user);
        CommentDto commentDto = CommentMapper.toCommentDto(comment);

        ItemDto itemDto1 = ItemMapper.toItemDto(item1);
        itemDto1.setLastBooking(null);
        itemDto1.setNextBooking(BookingMapper.toPartBookingDto(booking));
        itemDto1.setComments(List.of(CommentMapper.toCommentDto(comment)));

        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item1));
        when(bookingRepository.findAllByItemIdAndBookerIdAndEndBeforeOrderByStartDesc(anyInt(), anyInt(), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        Assertions.assertThat(itemService.writeComment(1, 1, commentDto)).isEqualTo(commentDto);
    }
}
