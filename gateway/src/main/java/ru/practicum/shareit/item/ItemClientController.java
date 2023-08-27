package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemClientController {
    private final ItemClient itemClient;
    private final String xSharer = "X-Sharer-User-Id";

    @GetMapping
    public ResponseEntity<Object> getAllItems(@RequestHeader(name = xSharer) Long userId,
                                              @PositiveOrZero @RequestParam(name = "from", required = false,
                                                      defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", required = false,
                                                      defaultValue = "10") Integer size) {
        log.info("Получен GET запрос на получении всех Items");
        return itemClient.getAllItems(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> readItemById(@RequestHeader(name = xSharer) Long userId,
                                               @PathVariable Long itemId) {
        log.info("Получен GET запрос на получении  Item по id {}", itemId);
        return itemClient.readItemById(userId, itemId);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(name = xSharer) Long userId,
                                             @RequestParam(name = "requestId", required = false) Long requestId,
                                             @RequestBody @Valid ItemDto itemDto) {
        log.info("Получен POST запрос на создание Item");
        return itemClient.createItem(userId, itemDto, requestId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(name = xSharer) Long userId,
                                             @PathVariable Long itemId,
                                             @RequestBody ItemDto itemDto) {
        log.info("Получен PATCH запрос на обновление Item");
        return itemClient.updateItem(itemId, userId, itemDto);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItemsByText(@RequestParam String text,
                                                    @RequestHeader(name = xSharer) Long userid,
                                                    @PositiveOrZero @RequestParam(name = "from",
                                                            required = false, defaultValue = "0")
                                                    Integer from,
                                                    @Positive @RequestParam(name = "size",
                                                            required = false, defaultValue = "10")
                                                    Integer size) {
        log.info("Получен GET запрос на создание поиск ITEM по тексту {}", text);
        return itemClient.searchItemsByText(text, userid, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(name = xSharer) Long userId,
                                             @PathVariable Long itemId,
                                             @RequestBody @Valid CommentRequestDto commentRequestDto) {
        log.info("Получен POST запрос на создание Comment от user {} к Item {}", userId, itemId);
        return itemClient.addComment(userId, itemId, commentRequestDto);
    }
}