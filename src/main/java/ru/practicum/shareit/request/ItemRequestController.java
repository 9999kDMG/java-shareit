package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private final String userIdHeader = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto postRequest(@RequestHeader(userIdHeader) int userId,
                                      @RequestBody @Valid ItemRequestDto itemRequestDto) {
        return itemRequestService.createRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getOwnerRequests(@RequestHeader(userIdHeader) int userId) {
        return itemRequestService.getAllByUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader(userIdHeader) int userId,
                                               @RequestParam(name = "from", required = false) Integer from,
                                               @RequestParam(name = "size", required = false) Integer size) {
        return itemRequestService.getAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader(userIdHeader) int userId,
                                         @PathVariable int requestId) {
        return itemRequestService.getRequestById(userId, requestId);
    }
}
