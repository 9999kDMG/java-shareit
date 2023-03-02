package ru.practicum.shareit.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.exception.BadRequestException;

public class Pagination {
    public static Pageable getPageOrThrow(Integer from, Integer size) {
        if (size == null || from == null) {
            return Pageable.unpaged();
        }
        if (size <= 0 || from < 0) {
            throw new BadRequestException("incorrect page parameters");
        }
        from = from / size;
        return PageRequest.of(from, size);
    }
}
