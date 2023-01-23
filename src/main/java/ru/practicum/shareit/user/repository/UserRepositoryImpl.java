package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;

import java.util.*;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final Map<Integer, User> users = new HashMap<>();
    private int globalId = 1;

    @Override
    public Optional<User> findById(int id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public int add(User user) {
        int userId = updateId();
        user.setId(userId);
        users.put(userId, user);
        return userId;
    }

    @Override
    public void overwrite(int id, User user) {
        users.put(id, user);
    }

    @Override
    public void delete(int id) {
        users.remove(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return users.values()
                .stream()
                .filter(p -> p.getEmail().equals(email))
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    private int updateId() {
        return globalId++;
    }
}
