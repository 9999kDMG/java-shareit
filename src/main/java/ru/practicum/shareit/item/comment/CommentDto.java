package ru.practicum.shareit.item.comment;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode

@Getter
@Setter
public class CommentDto {
    private Integer id;

    @NotBlank
    @Size(max = 1000)
    private String text;

    private String authorName;

    private LocalDateTime created;
}
