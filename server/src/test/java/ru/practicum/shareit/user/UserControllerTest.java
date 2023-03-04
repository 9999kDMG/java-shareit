package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.UtilsForTest;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());
    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setMockMvc() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(ErrorHandler.class)
                .build();
    }

    @Test
    void shouldReturnListUsers() throws Exception {
        List<User> users = List.of(
                UtilsForTest.makeUser(1),
                UtilsForTest.makeUser(2),
                UtilsForTest.makeUser(3)
        );

        when(userService.getAll()).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(users)));
    }

    @Test
    void shouldReturnEmptyList() throws Exception {
        List<User> users = List.of();

        when(userService.getAll()).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(users)));
    }

    @Test
    void shouldReturnNotFoundException() throws Exception {
        int userId = 1;

        when(userService.getUserById(userId))
                .thenThrow(new NotFoundException("not found user 1"));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnInternal() throws Exception {
        int userId = 1;

        when(userService.getUserById(userId))
                .thenThrow(new NotFoundException("not found user 1"));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnUser() throws Exception {
        int userId = 1;

        User user = UtilsForTest.makeUser(userId);

        when(userService.getUserById(userId)).thenReturn(user);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(user)));
    }

    @Test
    void shouldPostUser() throws Exception {
        int userId = 1;

        User user = UtilsForTest.makeUser(userId);
        String jsonUser = objectMapper.writeValueAsString(user);

        when(userService.create(user)).thenReturn(user);

        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(jsonUser))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(user)));
    }

    @Test
    void shouldUpdateUser() throws Exception {
        int userId = 1;

        User user = UtilsForTest.makeUser(userId);
        String jsonUser = objectMapper.writeValueAsString(user);

        when(userService.change(userId, user)).thenReturn(user);

        mockMvc.perform(patch("/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUser))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(user)));
    }

    @Test
    void shouldDeleteUser() throws Exception {
        int userId = 1;

        mockMvc.perform(delete("/users/" + userId))
                .andExpect(status().isOk());
    }

}
