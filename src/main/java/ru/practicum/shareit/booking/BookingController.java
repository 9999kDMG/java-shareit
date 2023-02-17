package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private final String userIdHeader = "X-Sharer-User-Id";

    @PostMapping
    public Booking postBooking(@RequestHeader(userIdHeader) int userId, @RequestBody BookingDto bookingDto) {
        return bookingService.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{id}")
    public Booking patchBooking(@RequestHeader(userIdHeader) int userId,
                                @PathVariable int id, @RequestParam boolean approved) {
        return bookingService.change(userId, id, approved);
    }

    @GetMapping("/{id}")
    public Booking getBookingById(@RequestHeader(userIdHeader) int userId, @PathVariable int id) {
        return bookingService.getBookingById(userId, id);
    }

    @GetMapping()
    public List<Booking> getBookingsByBooker(@RequestHeader(userIdHeader) int userId, @RequestParam(name = "state", defaultValue = "ALL") String state) {
        return bookingService.getBookingsByBooker(userId, state);
    }

    @GetMapping("/owner")
    public List<Booking> getBookingsByOwner(@RequestHeader(userIdHeader) int userId, @RequestParam(name = "state", defaultValue = "ALL") String state) {
        return bookingService.getBookingsByOwner(userId, state);
    }
}
