package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestPost;
import ru.practicum.shareit.utility.Utility;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestClientController {
    private final ItemRequestClient itemRequestClient;

    @GetMapping
    public ResponseEntity<Object> getItemRequests(@RequestHeader(value = Utility.SHARER) Long requesterId) {
        log.info("Получен GET запрос на получение списка своих запросов от user-a {}", requesterId);
        return itemRequestClient.getItemRequests(requesterId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@RequestHeader(value = Utility.SHARER) Long requesterId,
                                                     @Positive @PathVariable Long requestId) {

        log.info("Получен GET запрос на получение данных кокретного запроса по его id {} от user-a {}", requestId,
                requesterId);
        return itemRequestClient.getItemRequestById(requesterId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests(@RequestHeader(value = Utility.SHARER) Long requesterId,
                                                     @RequestParam(name = "from", required = false, defaultValue = "0")
                                                     @PositiveOrZero Integer from,
                                                     @Positive @RequestParam(name = "size", required = false,
                                                             defaultValue = "10")
                                                     Integer size) {
        log.info("олучен GET запрос на получение запросов от юзера userId={}, from={}, size={}", requesterId, from, size);
        return itemRequestClient.getAllItemRequests(requesterId, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader(value = Utility.SHARER) Long requesterId,
                                                    @RequestBody @Valid ItemRequestPost itemRequestPost) {
        log.info("Получен POST запрос на создание itemRequest от user-a {}", requesterId);
        return itemRequestClient.createItemRequest(requesterId, itemRequestPost);
    }
}