package ru.practicum.shareit.item.comment;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode

@Getter
@Setter
public class CommentDto {
    private Integer id;
    private String text;
    private String authorName;
    private LocalDateTime created;
}
