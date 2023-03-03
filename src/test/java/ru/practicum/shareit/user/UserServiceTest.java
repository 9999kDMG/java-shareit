package ru.practicum.shareit.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.UtilsForTest;
import ru.practicum.shareit.item.exception.ConflictException;
import ru.practicum.shareit.item.exception.NotFoundException;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService;

    @Test
    void shouldThrowNotFoundException() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());
        Assertions.assertThatThrownBy(() -> userService.getUserById(12)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldGetUser() {
        User user = UtilsForTest.makeUser(1);
        when(userRepository.findById(1)).thenReturn(Optional.ofNullable(user));
        Assertions.assertThat(userService.getUserById(1)).isEqualTo(user);
    }

    @Test
    void shouldCreateUser() {
        User user = UtilsForTest.makeUser(1);
        when(userRepository.save(any(User.class))).thenReturn(user);
        Assertions.assertThat(userService.create(user)).isEqualTo(user);
    }

    @Test
    void shouldGetAllUsers() {
        User first = UtilsForTest.makeUser(1);
        User second = UtilsForTest.makeUser(2);
        User third = UtilsForTest.makeUser(3);

        when(userRepository.findAll()).thenReturn(List.of(first, second, third));
        Assertions.assertThat(userService.getAll()).isEqualTo(List.of(first, second, third));
    }

    @Test
    void shouldThrowConflictException() {
        User first = UtilsForTest.makeUser(1);
        String email = first.getEmail();
        User second = UtilsForTest.makeUser(2);
        User newUser = UtilsForTest.makeUser(3);

        when(userRepository.findById(1)).thenReturn(Optional.of(first));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(second));
        Assertions.assertThatThrownBy(() -> userService.change(1, newUser)).isInstanceOf(ConflictException.class);
    }

    @Test
    void shouldUpdateUser() {
        User user = UtilsForTest.makeUser(1);
        String email = user.getEmail();
        User newUser = UtilsForTest.makeUser(3);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);
        Assertions.assertThat(userService.change(1, newUser)).isEqualTo(user);
    }

    @Test
    void shouldDeleteUser() {
        userService.delete(1);
        verify(userRepository, Mockito.times(1)).deleteById(1);
    }
}
