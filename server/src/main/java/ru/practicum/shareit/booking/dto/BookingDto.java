package ru.practicum.shareit.booking.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class BookingDto {
    private Integer itemId;
    private LocalDateTime start;
    private LocalDateTime end;
}
