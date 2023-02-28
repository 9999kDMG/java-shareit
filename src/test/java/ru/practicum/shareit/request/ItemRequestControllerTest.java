package ru.practicum.shareit.request;

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
import ru.practicum.shareit.item.exception.ErrorHandler;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemRequestControllerTest {
    private final String userIdHeader = "X-Sharer-User-Id";

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());
    private MockMvc mockMvc;

    @Mock
    private ItemRequestService itemRequestService;

    @InjectMocks
    private ItemRequestController itemRequestController;

    @BeforeEach
    void setMockMvc() {
        mockMvc = MockMvcBuilders.standaloneSetup(itemRequestController)
                .setControllerAdvice(ErrorHandler.class)
                .build();
    }

    @Test
    void shouldPostItemRequest() throws Exception {
        int userId = 1;
        User user = UtilsForTest.makeUser(1);

        ItemRequest itemRequest = UtilsForTest.makeItemRequest(1, user);
        Item item1 = UtilsForTest.makeItem(1, true, user, itemRequest);

        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        itemRequestDto.setItems(List.of(ItemMapper.toItemDto(item1)));

        String json = objectMapper.writeValueAsString(itemRequestDto);

        when(itemRequestService.createRequest(anyInt(), any(ItemRequestDto.class))).thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                        .header(userIdHeader, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemRequestDto)));
    }

    @Test
    void shouldReturnListItemRequestByUser() throws Exception {
        User user = UtilsForTest.makeUser(1);

        ItemRequest itemRequest = UtilsForTest.makeItemRequest(1, user);
        ItemRequest itemRequest2 = UtilsForTest.makeItemRequest(2, user);

        Item item1 = UtilsForTest.makeItem(1, true, user, itemRequest);
        Item item2 = UtilsForTest.makeItem(2, true, user, itemRequest2);

        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        itemRequestDto.setItems(List.of(ItemMapper.toItemDto(item1)));
        ItemRequestDto itemRequestDto2 = ItemRequestMapper.toItemRequestDto(itemRequest2);
        itemRequestDto2.setItems(List.of(ItemMapper.toItemDto(item2)));
        List<ItemRequestDto> itemRequestDtos = List.of(itemRequestDto2, itemRequestDto);

        when(itemRequestService.getAllByUser(1)).thenReturn(itemRequestDtos);

        mockMvc.perform(get("/requests")
                        .header(userIdHeader, 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemRequestDtos)));
    }

    @Test
    void shouldReturnListItemRequest() throws Exception {
        User user = UtilsForTest.makeUser(1);

        ItemRequest itemRequest = UtilsForTest.makeItemRequest(1, user);
        ItemRequest itemRequest2 = UtilsForTest.makeItemRequest(2, user);

        Item item1 = UtilsForTest.makeItem(1, true, user, itemRequest);
        Item item2 = UtilsForTest.makeItem(2, true, user, itemRequest2);

        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        itemRequestDto.setItems(List.of(ItemMapper.toItemDto(item1)));
        ItemRequestDto itemRequestDto2 = ItemRequestMapper.toItemRequestDto(itemRequest2);
        itemRequestDto2.setItems(List.of(ItemMapper.toItemDto(item2)));
        List<ItemRequestDto> itemRequestDtos = List.of(itemRequestDto2, itemRequestDto);

        when(itemRequestService.getAll(1, 0, 10)).thenReturn(itemRequestDtos);

        mockMvc.perform(get("/requests/all")
                        .queryParam("from", "0")
                        .queryParam("size", "10")
                        .header(userIdHeader, 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemRequestDtos)));
    }

    @Test
    void shouldReturnItemById() throws Exception {
        User user = UtilsForTest.makeUser(1);
        ItemRequest itemRequest = UtilsForTest.makeItemRequest(1, user);
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);

        when(itemRequestService.getRequestById(1, 1)).thenReturn(itemRequestDto);

        mockMvc.perform(get("/requests/" + 1)
                        .header(userIdHeader, 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemRequestDto)));
    }

}
