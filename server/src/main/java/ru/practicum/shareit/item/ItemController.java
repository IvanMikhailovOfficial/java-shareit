package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comments.dto.CommentRequestDto;
import ru.practicum.shareit.comments.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    private final String xSharer = "X-Sharer-User-Id";

    @GetMapping
    public ResponseEntity<List<ItemResponseDto>> getAllItems(@RequestHeader(xSharer) Long userId) {
        log.info("Получен GET запрос на получении всех Items");
        return new ResponseEntity<>(itemService.findAllItemByUserId(userId), HttpStatus.OK);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemResponseDto> readItemById(@RequestHeader(xSharer) Long userId,
                                                        @PathVariable Long itemId) {

        log.info("Получен GET запрос на получении  Item по id {}", itemId);
        return new ResponseEntity<>(itemService.findById(itemId, userId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ItemDto> createItem(@RequestHeader(xSharer) Long userId,
                                              @RequestBody  ItemDto itemDto,
                                              BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        log.info("Получен POST запрос на создание Item");
        return new ResponseEntity<>(itemService.save(userId, itemDto), HttpStatus.OK);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@RequestHeader(xSharer) Long userId,
                                              @PathVariable Long itemId,
                                              @RequestBody ItemDto itemDto) {

        log.info("Получен PATCH запрос на обновление Item");
        return new ResponseEntity<>(itemService.patch(itemId, userId, itemDto), HttpStatus.OK);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<String> deleteItemById(@PathVariable Long itemId) {
        itemService.delete(itemId);
        log.info("Получен DELETE запрос на удаление Item  c id {}", itemId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItemsByText(@RequestParam String text) {
        log.info("Получен GET запрос на создание поиск ITEM по тексту {}", text);
        return new ResponseEntity<>(itemService.findByText(text), HttpStatus.OK);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentResponseDto> addComment(
            @RequestHeader(value = "X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId,
            @RequestBody CommentRequestDto commentRequestDto
    ) {
        log.info("Получен POST запрос на создание Comment от user {} к Item {}", userId, itemId);
        return new ResponseEntity<>(itemService.addComment(userId, itemId, commentRequestDto), HttpStatus.OK);
    }
}