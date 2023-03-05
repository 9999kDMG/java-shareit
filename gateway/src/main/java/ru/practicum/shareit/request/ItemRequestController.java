package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;
    private final String userIdHeader = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> postRequest(@RequestHeader(userIdHeader) int userId,
                                              @RequestBody @Valid ItemRequestDto itemRequestDto) {
        return itemRequestClient.createRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnerRequests(@RequestHeader(userIdHeader) int userId) {
        return itemRequestClient.getAllByUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader(userIdHeader) int userId,
                                                 @RequestParam(name = "from", required = false) Integer from,
                                                 @RequestParam(name = "size", required = false) Integer size) {
        if (size == null || from == null) {
            Object voidObject = List.of();
            return new ResponseEntity<>(voidObject, HttpStatus.OK);
        }
        return itemRequestClient.getAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader(userIdHeader) int userId,
                                                 @PathVariable int requestId) {
        return itemRequestClient.getRequestById(userId, requestId);
    }
}
