package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.exception.BadRequestException;
import ru.practicum.shareit.item.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    public ItemRequestDto createRequest(int userId, ItemRequestDto itemRequestDto) {
        User user = userService.getUserById(userId);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setUser(user);
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    public List<ItemRequestDto> getAllByUser(int userId) {
        userService.getUserById(userId);
        Map<Integer, List<ItemDto>> itemDtos = itemRepository.findAllByRequestIdNotNull()
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.groupingBy(ItemDto::getRequestId));

        return itemRequestRepository.findAllByUserIdOrderByCreatedDesc(userId)
                .stream()
                .map(itemRequest -> {
                    ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
                    if (itemDtos.containsKey(itemRequestDto.getId())) {
                        itemRequestDto.setItems(itemDtos.get(itemRequestDto.getId()));
                    } else {
                        itemRequestDto.setItems(List.of());
                    }
                    return itemRequestDto;
                })
                .collect(Collectors.toList());
    }

    public List<ItemRequestDto> getAll(int userId, Integer from, Integer size) {
        if (size == null || from == null) {
            return List.of();
        }
        if (size <= 0 || from < 0) {
            throw new BadRequestException("incorrect page parameters");
        }
        userService.getUserById(userId);
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        Pageable page = PageRequest.of(from, size, sort);
        Map<Integer, List<ItemDto>> itemDtos = itemRepository.findAllWithRequestNotNull()
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.groupingBy(ItemDto::getRequestId));

        return itemRequestRepository.findAllByUserIdIsNot(userId, page)
                .stream()
                .map(itemRequest -> {
                    ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
                    itemRequestDto.setItems(itemDtos.get(itemRequestDto.getId()));
                    return itemRequestDto;
                })
                .collect(Collectors.toList());
    }

    public ItemRequestDto getRequestById(int userId, int requestId) {
        userService.getUserById(userId);
        List<ItemDto> itemDtos = itemRepository.findAllByRequestId(requestId)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());

        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Request id N%s", requestId)));
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        itemRequestDto.setItems(itemDtos);
        return itemRequestDto;
    }

}
