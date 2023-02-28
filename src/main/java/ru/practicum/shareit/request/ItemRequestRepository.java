package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ItemRequestRepository extends PagingAndSortingRepository<ItemRequest, Integer> {
    List<ItemRequest> findAllByUserIdOrderByCreatedDesc(int userId);

    List<ItemRequest> findAllByUserIdIsNot(int userId, Pageable page);
}
