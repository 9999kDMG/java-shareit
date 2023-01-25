package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private final Map<Integer, Item> items = new HashMap<>();
    private Integer globalId = 1;

    @Override
    public int add(Item item) {
        int itemId = updateId();
        item.setId(itemId);
        items.put(itemId, item);
        return itemId;
    }

    @Override
    public Optional<Item> findById(int id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public Stream<Item> findAll() {
        return items.values().stream();
    }

    @Override
    public void overwrite(int id, Item item) {
        items.put(id, item);
    }

    @Override
    public void delete(int id) {
        items.remove(id);
    }

    private int updateId() {
        return globalId++;
    }
}
