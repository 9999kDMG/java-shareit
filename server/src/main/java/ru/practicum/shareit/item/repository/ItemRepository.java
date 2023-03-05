package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    List<Item> findAllByOwnerIdOrderById(int id, Pageable page);

    @Query("SELECT i " +
            "FROM Item i " +
            "WHERE (LOWER(i.name) LIKE LOWER(CONCAT('%', ?1, '%')) " +
            "OR LOWER(i.description) LIKE LOWER(CONCAT('%', ?1, '%')) ) " +
            "AND i.available IS TRUE")
    List<Item> findAllByText(String text, Pageable page);

    @Query("SELECT i " +
            "FROM Item i " +
            "WHERE i.request IS NOT NULL")
    List<Item> findAllWithRequestNotNull();

    List<Item> findAllByRequestId(int requestId);

    List<Item> findAllByRequestIdNotNull();
}
