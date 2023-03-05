package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    private final String userIdHeader = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> postItem(@RequestHeader(userIdHeader) int userId, @Valid @RequestBody ItemDto itemDto) {
        return itemClient.createItem(userId, itemDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItem(@RequestHeader(userIdHeader) int userId, @PathVariable int id) {
        return itemClient.getItemById(userId, id);
    }

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader(userIdHeader) int userId,
                                         @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                         @Positive @RequestParam(name = "size", defaultValue = "30") Integer size) {
        return itemClient.getAllItemsUser(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestHeader(userIdHeader) int userId,
                                         @RequestParam String text,
                                         @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                         @Positive @RequestParam(name = "size", defaultValue = "30") Integer size) {
        return itemClient.searchByText(userId, text, from, size);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> patchItem(@RequestHeader(userIdHeader) int userId, @PathVariable int id, @RequestBody ItemDto itemDto) {
        return itemClient.change(userId, id, itemDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteItem(@RequestHeader(userIdHeader) int userId, @PathVariable int id) {
        return itemClient.delete(userId, id);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> postComment(@RequestHeader(userIdHeader) int userId,
                                              @PathVariable int itemId, @RequestBody @Valid CommentDto commentDto) {
        return itemClient.writeComment(userId, itemId, commentDto);
    }
}
