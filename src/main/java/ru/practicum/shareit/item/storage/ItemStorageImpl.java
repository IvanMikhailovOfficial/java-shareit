package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.exp.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapperDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.*;

@Repository
@Slf4j
@RequiredArgsConstructor
public class ItemStorageImpl implements ItemStorage {
    private final UserStorage userStorage;
    private final Map<Long, Item> items = new HashMap<>();
    private static Long index = 1L;

    @Override
    public Optional<Item> save(Long userId, ItemDto itemDto) {
        User user = userStorage.findById(userId).orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        Item item = ItemMapperDto.fromItemDto(itemDto);
        item.setOwner(user);
        item.setId(index);
        items.put(item.getId(), item);
        increasedId();

        return Optional.of(item);
    }

    private static void increasedId() {
        index++;
    }

    @Override
    public Optional<Item> patch(Long id, ItemDto itemDto) {
        Optional<Item> optionalItem = findById(itemDto.getId());

        if (optionalItem.isPresent()) {
            Item item = optionalItem.get();

            if (itemDto.getName() != null) {
                item.setName(itemDto.getName());
            }
            if (itemDto.getDescription() != null) {
                item.setDescription(itemDto.getDescription());
            }
            if (itemDto.getAvailable() != null) {
                item.setAvailable(itemDto.getAvailable());
            }

            items.put(item.getId(), item);

            return Optional.of(item);
        }

        return Optional.empty();
    }

    @Override
    public Optional<Item> findById(Long id) {
        Item item = items.get(id);
        return Optional.ofNullable(item);
    }

    @Override
    public void delete(Long id) {
        if (!items.containsKey(id)) {
            throw new EntityNotFoundException("Товар не найден");
        }
        items.remove(id);
    }

    @Override
    public List<Item> findAllItemByUserId(Long userId) {
        if (userStorage.findById(userId).isEmpty()) {
            throw new EntityNotFoundException("Пользователь не найден");
        }

        List<Item> foundItems = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwner().getId().equals(userId)) {
                foundItems.add(item);
            }
        }

        return foundItems;
    }

    @Override
    public List<Item> findByText(String text) {
        if (text.isBlank() || text.isEmpty()) {
            return new ArrayList<>();
        }

        List<Item> foundItems = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.isAvailable() && (item.getName().toLowerCase().contains(text.toLowerCase())
                    || item.getDescription().toLowerCase().contains(text.toLowerCase()))) {
                foundItems.add(item);
            }
        }

        return foundItems;
    }
}