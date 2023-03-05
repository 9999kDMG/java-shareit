package ru.practicum.shareit.request;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.UtilsForTest;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {
    @Mock
    ItemRequestRepository itemRequestRepository;

    @Mock
    UserService userService;

    @Mock
    ItemRepository itemRepository;

    @InjectMocks
    ItemRequestService itemRequestService;

    @Test
    void shouldCreateItemRequest() {
        User user = UtilsForTest.makeUser(1);
        ItemRequest itemRequest = UtilsForTest.makeItemRequest(1, user);
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);

        when(userService.getUserById(anyInt())).thenReturn(user);
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);
        Assertions.assertThat(itemRequestService.createRequest(1, itemRequestDto)).isEqualTo(itemRequestDto);
    }

    @Test
    void shouldReturnAllItemRequest() {
        User user = UtilsForTest.makeUser(1);

        ItemRequest itemRequest = UtilsForTest.makeItemRequest(1, user);
        ItemRequest itemRequest2 = UtilsForTest.makeItemRequest(2, user);
        int from = 0;
        int size = 10;
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        Pageable page = PageRequest.of(from, size, sort);

        Item item1 = UtilsForTest.makeItem(1, true, user, itemRequest);
        Item item2 = UtilsForTest.makeItem(2, true, user, itemRequest2);
        List<Item> items = List.of(item1, item2);

        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        itemRequestDto.setItems(List.of(ItemMapper.toItemDto(item1)));
        ItemRequestDto itemRequestDto2 = ItemRequestMapper.toItemRequestDto(itemRequest2);
        itemRequestDto2.setItems(List.of(ItemMapper.toItemDto(item2)));
        List<ItemRequestDto> itemRequestDtos = List.of(itemRequestDto2, itemRequestDto);

        when(userService.getUserById(anyInt())).thenReturn(user);
        when(itemRepository.findAllWithRequestNotNull()).thenReturn(items);
        when(itemRequestRepository.findAllByUserIdIsNot(user.getId(), page)).thenReturn(List.of(itemRequest2, itemRequest));

        Assertions.assertThat(itemRequestService.getAll(1, from, size)).isEqualTo(itemRequestDtos);
    }

    @Test
    void shouldReturnAllItemRequestByUser() {
        int userId = 1;
        User user = UtilsForTest.makeUser(userId);
        User user2 = UtilsForTest.makeUser(2);

        ItemRequest itemRequest = UtilsForTest.makeItemRequest(1, user);
        ItemRequest itemRequest2 = UtilsForTest.makeItemRequest(2, user2);

        Item item1 = UtilsForTest.makeItem(1, true, user, itemRequest);
        Item item2 = UtilsForTest.makeItem(2, true, user, itemRequest2);
        List<Item> items = List.of(item1, item2);

        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        itemRequestDto.setItems(List.of(ItemMapper.toItemDto(item1)));


        List<ItemRequestDto> itemRequestDtos = List.of(itemRequestDto);

        when(userService.getUserById(anyInt())).thenReturn(user);
        when(itemRepository.findAllByRequestIdNotNull()).thenReturn(items);
        when(itemRequestRepository.findAllByUserIdOrderByCreatedDesc(userId)).thenReturn(List.of(itemRequest));

        Assertions.assertThat(itemRequestService.getAllByUser(userId)).isEqualTo(itemRequestDtos);
    }

    @Test
    void shouldReturnRequestById() {
        int userId = 1;
        User user = UtilsForTest.makeUser(userId);

        ItemRequest itemRequest = UtilsForTest.makeItemRequest(1, user);
        ItemRequest itemRequest2 = UtilsForTest.makeItemRequest(2, user);

        Item item1 = UtilsForTest.makeItem(1, true, user, itemRequest);
        Item item2 = UtilsForTest.makeItem(2, true, user, itemRequest2);
        List<Item> items = List.of(item1, item2);

        ItemRequestDto itemRequestDto2 = ItemRequestMapper.toItemRequestDto(itemRequest2);
        itemRequestDto2.setItems(List.of(ItemMapper.toItemDto(item1), ItemMapper.toItemDto(item2)));

        when(userService.getUserById(anyInt())).thenReturn(user);
        when(itemRepository.findAllByRequestId(2)).thenReturn(items);
        when(itemRequestRepository.findById(2)).thenReturn(Optional.of(itemRequest2));

        Assertions.assertThat(itemRequestService.getRequestById(userId, 2)).isEqualTo(itemRequestDto2);
    }


}
