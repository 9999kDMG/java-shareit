package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.UtilsForTest;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {
    private final String userIdHeader = "X-Sharer-User-Id";

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());
    private MockMvc mockMvc;

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    @BeforeEach
    void setMockMvc() {
        mockMvc = MockMvcBuilders.standaloneSetup(itemController)
                .setControllerAdvice(ErrorHandler.class)
                .build();
    }

    @Test
    void shouldPostItem() throws Exception {
        int userId = 1;

        ItemDto itemDto = new ItemDto(1, "test", "test item", true,
                null, null, null, 1);

        String jsonItemDto = objectMapper.writeValueAsString(itemDto);

        when(itemService.createItem(anyInt(), any(ItemDto.class))).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header(userIdHeader, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonItemDto))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemDto)));
    }

    @Test
    void shouldReturnNotFound() throws Exception {
        int userId = 1;

        ItemDto itemDto = new ItemDto(1, "test", "test item", true,
                null, null, null, 1);

        String jsonItemDto = objectMapper.writeValueAsString(itemDto);

        when(itemService.createItem(anyInt(), any(ItemDto.class))).thenThrow(new NotFoundException("user 1 NotFound"));

        mockMvc.perform(post("/items")
                        .header(userIdHeader, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonItemDto))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnListItems() throws Exception {
        ItemDto itemDto1 = UtilsForTest.makeItemDto(1, true, 1);
        ItemDto itemDto2 = UtilsForTest.makeItemDto(2, true, 2);
        ItemDto itemDto3 = UtilsForTest.makeItemDto(3, true, 3);

        List<ItemDto> itemsDto = List.of(itemDto1, itemDto2, itemDto3);

        Pageable page = PageRequest.of(0, 3);
        when(itemService.getAllItemsUser(1, page)).thenReturn(itemsDto);

        mockMvc.perform(get("/items")
                        .queryParam("from", "0")
                        .queryParam("size", "3")
                        .header(userIdHeader, 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemsDto)));
    }

    @Test
    void shouldReturnListItemsFullPage() throws Exception {
        ItemDto itemDto1 = UtilsForTest.makeItemDto(1, true, 1);
        ItemDto itemDto2 = UtilsForTest.makeItemDto(2, true, 2);
        ItemDto itemDto3 = UtilsForTest.makeItemDto(3, true, 3);

        List<ItemDto> itemsDto = List.of(itemDto1, itemDto2, itemDto3);

        Pageable page = Pageable.unpaged();
        when(itemService.getAllItemsUser(1, page)).thenReturn(itemsDto);

        mockMvc.perform(get("/items")
                        .header(userIdHeader, 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemsDto)));
    }

    @Test
    void shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/items")
                        .header(userIdHeader, 1)
                        .queryParam("from", "-1")
                        .queryParam("size", "0")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnItemById() throws Exception {
        int itemId = 1;
        ItemDto itemDto1 = UtilsForTest.makeItemDto(itemId, true, 1);

        when(itemService.getItemById(1, itemId)).thenReturn(itemDto1);

        mockMvc.perform(get("/items/" + itemId)
                        .header(userIdHeader, 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemDto1)));
    }

    @Test
    void shouldReturnItemByTextSearch() throws Exception {
        int userId = 1;
        ItemDto itemDto1 = UtilsForTest.makeItemDto(1, true, 1);
        ItemDto itemDto2 = UtilsForTest.makeItemDto(2, true, 2);
        ItemDto itemDto3 = UtilsForTest.makeItemDto(3, true, 3);

        List<ItemDto> itemsDto = List.of(itemDto1, itemDto2, itemDto3);
        String searchText = "item";

        Pageable page = Pageable.unpaged();
        when(itemService.searchByText(userId, searchText, page)).thenReturn(itemsDto);

        mockMvc.perform(get("/items/search")
                        .header(userIdHeader, userId)
                        .queryParam("text", searchText))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemsDto)));
    }

    @Test
    void shouldDeleteItem() throws Exception {
        int itemId = 1;
        int userId = 1;

        mockMvc.perform(delete("/items/" + itemId).header(userIdHeader, userId))
                .andExpect(status().isOk());
    }

    @Test
    void shouldUpdateUser() throws Exception {
        int userId = 1;
        int itemId = 1;
        ItemDto itemDto1 = UtilsForTest.makeItemDto(1, true, 1);
        String jsonItemDto = objectMapper.writeValueAsString(itemDto1);

        when(itemService.change(anyInt(), anyInt(), any(ItemDto.class))).thenReturn(itemDto1);

        mockMvc.perform(patch("/items/" + itemId)
                        .header(userIdHeader, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonItemDto))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemDto1)));
    }

    @Test
    void shouldCreateCommentToItem() throws Exception {
        int userId = 1;
        int itemId = 1;
        CommentDto commentDto = new CommentDto(1, "comment for Test", "name", LocalDateTime.now());
        String json = objectMapper.writeValueAsString(commentDto);

        when(itemService.writeComment(anyInt(), anyInt(), any(CommentDto.class))).thenReturn(commentDto);

        mockMvc.perform(post("/items/" + itemId + "/comment")
                        .header(userIdHeader, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(commentDto)));
    }
}
