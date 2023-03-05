package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.PartBookingDto;

public class BookingMapper {
    public static Booking toBooking(BookingDto bookingDto) {
        return new Booking(null, bookingDto.getStart(),
                bookingDto.getEnd(), null, null, null);
    }

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getItem().getId(),
                booking.getStart(),
                booking.getEnd());
    }

    public static PartBookingDto toPartBookingDto(Booking booking) {
        return new PartBookingDto(booking.getId(), booking.getBooker().getId());
    }
}
