package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode

@Setter
@Getter

public class ItemRequestDto {
    private Integer id;
    private LocalDateTime created;
    private String description;
    private List<ItemDto> items;
}
