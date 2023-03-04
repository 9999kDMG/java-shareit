package ru.practicum.shareit.user;

import lombok.*;

import javax.persistence.*;

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

    @Column(name = "name", length = 20)
    private String name;

    @Column(unique = true, nullable = false, length = 50)
    private String email;
}
