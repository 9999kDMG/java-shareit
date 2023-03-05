package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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

    @NotNull
    @NotBlank
    @Size(max = 500)
    private String description;

    List<ItemDto> items;
}
