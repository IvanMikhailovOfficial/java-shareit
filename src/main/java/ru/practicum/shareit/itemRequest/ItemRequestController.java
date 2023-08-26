package ru.practicum.shareit.itemRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.itemRequest.dto.ItemRequestInputDto;
import ru.practicum.shareit.itemRequest.dto.ItemRequestOutputDto;
import ru.practicum.shareit.itemRequest.service.ItemRequestService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    private final String xSharer = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<ItemRequestOutputDto> createItemRequest(@RequestHeader(xSharer) Long userId,
                                                                  @Valid @RequestBody ItemRequestInputDto itemRequestInputDto) {
        log.info("Получен POST запрос на создание itemRequest от user-a {}", userId);
        return new ResponseEntity<>(itemRequestService.createItemRequest(userId, itemRequestInputDto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestOutputDto>> getItemRequest(@RequestHeader(xSharer) Long userId) {
        log.info("Получен GET запрос на получение списка своих запросов от user-a {}", userId);
        return new ResponseEntity<>(itemRequestService.getItemRequest(userId), HttpStatus.OK);
    }

    @GetMapping("all")
    public ResponseEntity<List<ItemRequestOutputDto>> getItemRequestsByOtherUsers(
            @RequestHeader(xSharer) Long userId,
            @RequestParam(name = "from", required = false) Integer from,
            @RequestParam(name = "size", required = false) Integer size) {
        log.info("Получен GET запрос на получение списка запросов созданных другими пользователями от " +
                "user-a с id  {}", userId);
        return new ResponseEntity<>(itemRequestService.getItemRequestsByOtherUsers(userId, from, size), HttpStatus.OK);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestOutputDto> getItemRequestsByOtherUsers(@RequestHeader(xSharer) Long userId,
                                                                            @PathVariable Long requestId) {
        log.info("Получен GET запрос на получение данных кокретного запроса по его id {} от user-a {}", requestId,
                userId);
        return new ResponseEntity<>(itemRequestService.getItemRequestsByIdOwner(userId, requestId), HttpStatus.OK);
    }
}