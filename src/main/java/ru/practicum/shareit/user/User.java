package ru.practicum.shareit.user;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * TODO Sprint add-controllers.
 */
@Builder
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class User {
    private Integer id;

    @NotNull
    @Size(max = 20)
    private String name;
    @NotNull
    @Email
    private String email;
}
