package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.item.validation.ItemValidator;

import java.util.List;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;

    @Autowired
    public ItemServiceImpl(ItemStorage itemStorage) {
        this.itemStorage = itemStorage;
    }

    @Override
    public ItemDto create(ItemDto itemDto, Long userId) {
        ItemValidator.itemValidate(itemDto);
        return ItemMapper.toItemDto(itemStorage.create(ItemMapper.toItem(itemDto), userId));
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long itemId, Long userId) {
        return ItemMapper.toItemDto(itemStorage.update(ItemMapper.toItem(itemDto), itemId, userId));
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public ItemDto get(Long itemId, Long userId) {
        return ItemMapper.toItemDto(itemStorage.get(itemId, userId));
    }

    @Override
    public List<ItemDto> getAll(Long userId) {
        return ItemMapper.toItemDtoList(itemStorage.getAll(userId));
    }

    @Override
    public List<ItemDto> search(String text, Long userId) {
        return ItemMapper.toItemDtoList(itemStorage.search(text, userId));
    }
}
