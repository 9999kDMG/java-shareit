package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.BadRequestException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;
    private final String userIdHeader = "X-Sharer-User-Id";

    @GetMapping
    public ResponseEntity<Object> getBookingsByBooker(@RequestHeader(userIdHeader) int userId,
                                                      @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                      @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                      @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = getStateOrThrow(stateParam);
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getBookingsByBooker(userId, state, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> postBooking(@RequestHeader(userIdHeader) int userId,
                                              @RequestBody @Valid BookingDto requestDto) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.postBooking(userId, requestDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader(userIdHeader) int userId,
                                                 @PathVariable Integer bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBookingById(userId, bookingId);
    }


    @PatchMapping("/{id}")
    public ResponseEntity<Object> patchBooking(@RequestHeader(userIdHeader) int userId,
                                               @PathVariable int id, @RequestParam boolean approved) {
        return bookingClient.change(userId, id, approved);
    }


    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByOwner(@RequestHeader(userIdHeader) int userId,
                                                     @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
                                                     @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                     @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = getStateOrThrow(stateParam);
        return bookingClient.getBookingsByOwner(userId, state, from, size);
    }

    private BookingState getStateOrThrow(String stateParam) {
        return BookingState.from(stateParam)
                .orElseThrow(() -> new BadRequestException("Unknown state: " + stateParam));
    }

}
