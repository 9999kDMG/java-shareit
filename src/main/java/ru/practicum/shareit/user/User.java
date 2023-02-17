package ru.practicum.shareit.user;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * TODO Sprint add-controllers.
 */
@Builder

@AllArgsConstructor
@NoArgsConstructor

@Getter
@Setter

@EqualsAndHashCode

@Entity
@Table(name = "users")
public class User {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Size(max = 20)
    @Column(name = "name", length = 20)
    private String name;

    @NotNull
    @Email
    @Column(unique = true, nullable = false, length = 50)
    private String email;
}
