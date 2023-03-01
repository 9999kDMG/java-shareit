package ru.practicum.shareit.request;

import lombok.*;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@NoArgsConstructor
@AllArgsConstructor

@Setter
@Getter

@Builder

@Entity
@Table(name = "item_request")
public class ItemRequest {
    @Id
    @Column(name = "item_request_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;

    @Column(name = "description", nullable = false, length = 500)
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
