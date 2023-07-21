package ru.practicum.shareit.item.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RequestMapping("/items")
@RestController
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ResponseEntity<ItemDto> create(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Поступил POST запрос create для обьекта item");
        return new ResponseEntity<>(itemService.create(itemDto, userId), HttpStatus.OK);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> update(@PathVariable Long itemId, @RequestBody ItemDto itemDto,
                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Поступил PATCH запрос update для объекта item");
        return new ResponseEntity<>(itemService.update(itemDto, itemId, userId), HttpStatus.OK);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> get(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Поступил GET запрос get для объекта item");
        System.out.println("Сообщение");
        return new ResponseEntity<>(itemService.get(itemId, userId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Поступил GET запрос getAll для всех объектов item");
        return new ResponseEntity<List<ItemDto>>(itemService.getAll(userId), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> search(@RequestParam("text") String text,
                                                @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Поступил GET запрос search для получения объекта item");
        return new ResponseEntity<List<ItemDto>>(itemService.search(text, userId), HttpStatus.OK);
    }
}
