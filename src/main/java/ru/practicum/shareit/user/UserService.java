package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User create(User user) {
        userRepository.findByEmail(user.getEmail())
                .ifPresent(p -> {
                    throw new DuplicateEmailException(String.format("email %s", p.getEmail()));
                });
        int idInDB = userRepository.add(user);
        return userRepository.findById(idInDB)
                .orElseThrow(() -> new NotFoundException(String.format("user id N%s", idInDB)));
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
        userRepository.findByEmail(user.getEmail())
                .ifPresent(p -> {
                    throw new DuplicateEmailException(String.format("email %s", p.getEmail()));
                });
        if (user.getName() != null) {
            userInDb.setName(user.getName());
        }
        if (user.getEmail() != null) {
            userInDb.setEmail(user.getEmail());
        }
        userRepository.overwrite(id, userInDb);
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("user id N%s", id)));
    }

    public void delete(int id) {
        userRepository.delete(id);
    }
}
