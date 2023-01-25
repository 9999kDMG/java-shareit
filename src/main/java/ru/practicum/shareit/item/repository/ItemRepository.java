package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.Optional;
import java.util.stream.Stream;

public interface ItemRepository {
    int add(Item item);

    Optional<Item> findById(int id);

    Stream<Item> findAll();

    void overwrite(int id, Item item);

    void delete(int id);
}
