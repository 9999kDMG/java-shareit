package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(int id);

    int add(User user);

    void overwrite(int id, User user);

    void delete(int id);

    Optional<User> findByEmail(String email);

    List<User> findAll();
}
