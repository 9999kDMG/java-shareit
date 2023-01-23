package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public ItemDto createItem(int userId, ItemDto itemDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("user id N%s", userId)));
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user);
        int itemId = itemRepository.add(item);
        return ItemMapper.toItemDto(itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("item id N%s", itemId))));
    }

    public ItemDto getItemById(int userId, int id) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("user id N%s", userId)));
        return ItemMapper.toItemDto(itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("item id N%s", id))));
    }

    public List<ItemDto> getAllItemsUser(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("user id N%s", userId)));
        return itemRepository.findAll().filter(i -> user.equals(i.getOwner()))
                .map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    public void delete(int userId, int id) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("user id N%s", userId)));
        itemRepository.delete(id);
    }

    public ItemDto change(int userId, int id, ItemDto itemDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("user id N%s", userId)));
        Item itemInDb = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("user id N%s", userId)));
        if (!user.equals(itemInDb.getOwner())) {
            throw new NotFoundException(String.format("the item id N%s has a different owner", id));
        }
        if (itemDto.getName() != null) {
            itemInDb.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            itemInDb.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            itemInDb.setAvailable(itemDto.getAvailable());
        }

        itemRepository.overwrite(id, itemInDb);

        return ItemMapper.toItemDto(itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("item id N%s", id))));
    }

    public List<ItemDto> searchByText(int userId, String text) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("user id N%s", userId)));
        if (text != null && !text.isBlank()) {
            return itemRepository.findAll().filter(item -> isTextContained(text, item))
                    .map(ItemMapper::toItemDto).collect(Collectors.toList());
        } else {
            return List.of();
        }
    }

    private boolean isTextContained(String text, Item item) {
        boolean isNameContainsText = item.getName().toLowerCase().contains(text.strip().toLowerCase());
        boolean isDescriptionContainsText = item.getDescription().toLowerCase().contains(text.strip().toLowerCase());

        return item.getAvailable() && (isDescriptionContainsText || isNameContainsText);
    }
}
