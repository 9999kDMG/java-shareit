package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.exception.ConflictException;
import ru.practicum.shareit.item.exception.NotFoundException;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User create(User user) {
        return userRepository.save(user);
    }

    public User getUserById(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("user id N%s", userId)));
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User change(int id, User user) {
        User userInDb = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("user id N%s", id)));
        throwIfEmailExist(user.getEmail());

        if (user.getName() != null) {
            userInDb.setName(user.getName());
        }
        if (user.getEmail() != null) {
            userInDb.setEmail(user.getEmail());
        }

        return userRepository.save(userInDb);
    }

    public void delete(int id) {
        userRepository.deleteById(id);
    }

    private void throwIfEmailExist(String email) {
        userRepository.findByEmail(email)
                .ifPresent(p -> {
                    throw new ConflictException(String.format("email %s", p.getEmail()));
                });
    }
}
