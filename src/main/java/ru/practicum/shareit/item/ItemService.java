package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.PartBookingDto;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    public ItemDto createItem(int userId, ItemDto itemDto) {
        User user = getUserOtherThrow(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    public ItemDto getItemById(int userId, int id) {
        User user = getUserOtherThrow(userId);
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("item id N%s", id)));
        ItemDto itemDto = ItemMapper.toItemDto(item);
        if (item.getOwner().equals(user)) {
            List<Booking> bookings = bookingRepository.findAllByItemIdOrderByStart(id);
            PartBookingDto lastBooking = findLastBooking(bookings)
                    .map(BookingMapper::toPartBookingDto).orElse(null);
            PartBookingDto nextBooking = findNextBooking(bookings)
                    .map(BookingMapper::toPartBookingDto).orElse(null);
            itemDto.setLastBooking(lastBooking);
            itemDto.setNextBooking(nextBooking);
        }
        itemDto.setComments(commentRepository.findAllByItemIdOrderByCreatedDesc(id)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList()));
        return itemDto;
    }

    public List<ItemDto> getAllItemsUser(int userId) {
        getUserOtherThrow(userId);

        return itemRepository.findAllByOwnerId(userId)
                .stream()
                .map((item) -> {
                    ItemDto itemDto = ItemMapper.toItemDto(item);
                    List<Booking> bookings = bookingRepository.findAllByItemIdOrderByStart(item.getId());
                    PartBookingDto lastBooking = findLastBooking(bookings)
                            .map(BookingMapper::toPartBookingDto).orElse(null);
                    PartBookingDto nextBooking = findNextBooking(bookings)
                            .map(BookingMapper::toPartBookingDto).orElse(null);
                    itemDto.setLastBooking(lastBooking);
                    itemDto.setNextBooking(nextBooking);
                    itemDto.setComments(commentRepository.findAllByItemIdOrderByCreatedDesc(item.getId())
                            .stream()
                            .map(CommentMapper::toCommentDto)
                            .collect(Collectors.toList()));

                    return itemDto;
                })
                .collect(Collectors.toList());
    }

    public void delete(int userId, int id) {
        getUserOtherThrow(userId);
        itemRepository.deleteById(id);
    }

    public ItemDto change(int userId, int id, ItemDto itemDto) {
        User user = getUserOtherThrow(userId);
        Item itemInDb = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("item id N%s", id)));
        if (!user.equals(itemInDb.getOwner())) {
            throw new NotFoundException(String.format("the item id N%s has a different owner", id));
        }
        if (itemDto.getName() != null) {
            itemInDb.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            itemInDb.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            itemInDb.setAvailable(itemDto.getAvailable());
        }

        return ItemMapper.toItemDto(itemRepository.save(itemInDb));
    }

    public List<ItemDto> searchByText(int userId, String text) {
        getUserOtherThrow(userId);
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return itemRepository.findAllByText(text)
                .stream()
                .map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    public CommentDto writeComment(int userId, int itemId, CommentDto commentDto) {
        getUserOtherThrow(userId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("item id N%s", itemId)));
        List<Booking> bookings = bookingRepository
                .findAllByItemIdAndBookerIdAndEndBeforeOrderByStartDesc(item.getId(), userId, LocalDateTime.now());
        if (bookings.isEmpty()) {
            throw new BadRequestException(String.format("the user id N%s did not book the item", userId));
        }
        Comment comment = Comment.builder()
                .author(getUserOtherThrow(userId))
                .item(item)
                .text(commentDto.getText())
                .created(LocalDateTime.now())
                .build();

        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    private User getUserOtherThrow(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("user id N%s", userId)));
    }

    private Optional<Booking> findLastBooking(List<Booking> bookings) {
        return bookings.stream()
                .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                .reduce((first, second) -> second);
    }

    private Optional<Booking> findNextBooking(List<Booking> bookings) {
        return bookings.stream()
                .filter(booking -> booking.getEnd().isAfter(LocalDateTime.now()))
                .reduce((first, second) -> first);
    }
}
