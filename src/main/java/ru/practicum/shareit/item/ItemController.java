package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.utils.Pagination.getPageOrThrow;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    private final String userIdHeader = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto postItem(@RequestHeader(userIdHeader) int userId, @Valid @RequestBody ItemDto itemDto) {
        return itemService.createItem(userId, itemDto);
    }

    @GetMapping("/{id}")
    public ItemDto getItem(@RequestHeader(userIdHeader) int userId, @PathVariable int id) {
        return itemService.getItemById(userId, id);
    }

    @GetMapping
    public List<ItemDto> getAll(@RequestHeader(userIdHeader) int userId,
                                @RequestParam(name = "from", required = false) Integer from,
                                @RequestParam(name = "size", required = false) Integer size) {
        return itemService.getAllItemsUser(userId, getPageOrThrow(from, size));
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestHeader(userIdHeader) int userId,
                                @RequestParam String text,
                                @RequestParam(name = "from", required = false) Integer from,
                                @RequestParam(name = "size", required = false) Integer size) {
        return itemService.searchByText(userId, text, getPageOrThrow(from, size));
    }

    @PatchMapping("/{id}")
    public ItemDto patchItem(@RequestHeader(userIdHeader) int userId, @PathVariable int id, @RequestBody ItemDto itemDto) {
        return itemService.change(userId, id, itemDto);
    }

    @DeleteMapping("/{id}")
    public void deleteItem(@RequestHeader(userIdHeader) int userId, @PathVariable int id) {
        itemService.delete(userId, id);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto postComment(@RequestHeader(userIdHeader) int userId,
                                  @PathVariable int itemId, @RequestBody @Valid CommentDto commentDto) {
        return itemService.writeComment(userId, itemId, commentDto);
    }
}
