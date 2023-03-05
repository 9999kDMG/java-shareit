package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.PartBookingDto;
import ru.practicum.shareit.item.comment.CommentDto;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class ItemDto {
    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private PartBookingDto lastBooking;
    private PartBookingDto nextBooking;
    private List<CommentDto> comments;
    private Integer requestId;
}
