package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

/**
 * TODO Sprint add-controllers.
 */
@Builder
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Item {
    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private ItemRequest request;
    private User owner;
}
