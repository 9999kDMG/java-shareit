package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.PartBookingDto;
import ru.practicum.shareit.item.comment.CommentDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
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
    @NotNull
    @NotBlank
    private String name;
    @NotNull
    @NotBlank
    private String description;
    @NotNull
    private Boolean available;
    private PartBookingDto lastBooking;
    private PartBookingDto nextBooking;
    private List<CommentDto> comments;
    @PositiveOrZero
    private Integer requestId;
}
