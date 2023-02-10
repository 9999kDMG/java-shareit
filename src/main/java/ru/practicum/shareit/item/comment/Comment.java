package ru.practicum.shareit.item.comment;

import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "COMMENTS")
@Builder
@AllArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COMMENT_ID")
    private Integer id;
    private String text;
    @ManyToOne
    @JoinColumn(name = "ITEM_ID")
    private Item item;
    @ManyToOne
    @JoinColumn(name = "AUTHOR_ID")
    private User author;
    private LocalDateTime created;
}
