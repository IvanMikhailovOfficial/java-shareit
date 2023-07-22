package ru.practicum.shareit.item.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.exeptions.ItemNotAvailableException;
import ru.practicum.shareit.exeptions.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Component
@Slf4j
public class InMemoryItemStorage implements ItemStorage {

    private final HashMap<Long, Item> hashMap = new HashMap<>();

    private final UserStorage userStorage;

    @Autowired
    public InMemoryItemStorage(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    private long idGenerator = 1;

    @Override
    public Item create(Item item, Long userId) {
        if (item.getAvailable() == null) {
            throw new ItemNotAvailableException("Создать предмет со статусом \"Не доступен\" нельзя");
        }
        userStorage.userExist(userId);
        item.setOwner(userStorage.get(userId));
        item.setId(idGenerator);
        hashMap.put(idGenerator++, item);
        log.debug("Предмет с id {} успешно создан", item.getId());
        return item;
    }

    @Override
    public Item update(Item item, Long itemId, Long userId) {
        itemExist(itemId);
        userStorage.userExist(userId);
        Item oldItem = hashMap.get(itemId);
        if (!oldItem.getOwner().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Нет прав собственника на изменение данных");
        }

        if (item.getName() != null) {
            oldItem.setName(item.getName());
        }

        if (item.getDescription() != null) {
            oldItem.setDescription(item.getDescription());
        }

        if (item.getAvailable() != null) {
            oldItem.setAvailable(item.getAvailable());
        }
        return oldItem;
    }

    @Override
    public void delete(Long id) {

        if (hashMap.containsKey(id)) {
            hashMap.remove(id);
            log.debug("Предмет с id {} успешно удален", id);
        } else {
            throw new ItemNotFoundException("Предмета с таким id не существует");
        }
    }

    @Override
    public Item get(Long itemId, Long userId) {
        userStorage.userExist(userId);
        itemExist(itemId);
        log.debug("Предмет с id {} успешно получен пользователем с id {}", itemId, userId);
        return hashMap.get(itemId);
    }

    @Override
    public List<Item> getAll(Long userId) {
        userStorage.userExist(userId);
        log.debug("предметы успешно получены пользователем с id {}", userId);
        List<Item> list = new ArrayList<>();
        for (Item item : hashMap.values()) {
            if (item.getOwner().getId().equals(userId)) {
                list.add(item);
            }
        }
        return list;
    }

    @Override
    public List<Item> search(String text, Long userId) {
        userStorage.userExist(userId);
        List<Item> list = new ArrayList<>();
        if (text.isBlank()) {
            return list;
        }
        for (Item item : hashMap.values()) {
            if (item.getAvailable() && (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                    item.getDescription().toLowerCase().contains(text.toLowerCase()))) {
                list.add(item);
            }
        }
        return list;
    }

    public void itemExist(Long itemId) {
        if (!hashMap.containsKey(itemId)) {
            throw new ItemNotFoundException("Нет item'a с id " + itemId);
        }
    }
}

