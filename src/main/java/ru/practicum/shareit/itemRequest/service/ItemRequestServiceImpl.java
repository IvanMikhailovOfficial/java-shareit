package ru.practicum.shareit.itemRequest.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.exceptions.exp.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemMapperDto;
import ru.practicum.shareit.item.repositories.ItemRepository;
import ru.practicum.shareit.itemRequest.dto.ItemRequestInputDto;
import ru.practicum.shareit.itemRequest.dto.ItemRequestOutputDto;
import ru.practicum.shareit.itemRequest.mapper.ItemRequestMapper;
import ru.practicum.shareit.itemRequest.model.ItemRequest;
import ru.practicum.shareit.itemRequest.storage.ItemRequestRepository;
import ru.practicum.shareit.user.repositories.UserRepository;
import ru.practicum.shareit.utility.Utility;

import java.util.List;

@Service
@Repository
@Slf4j
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequestOutputDto createItemRequest(Long userId, ItemRequestInputDto itemRequestInputDto) {
        if (!userRepository.existsById(userId)) {
            log.info("user с id {} не найден", userId);
            throw new EntityNotFoundException("Юзер с id " + userId + " не найден");
        }
        return ItemRequestMapper.toOutputDto(itemRequestRepository.save(ItemRequestMapper.fromInputToItemRequest(userId,
                itemRequestInputDto)));
    }

    @Override
    public ItemRequestOutputDto getItemRequestsByIdOwner(Long userId, Long itemRequestId) {
        if (!userRepository.existsById(userId)) {
            log.info("user с id {} не найден", userId);
            throw new EntityNotFoundException("user с id " + userId + " не найден");
        }

        if (!itemRequestRepository.existsById(itemRequestId)) {
            log.info("itemRequest c id {} не найден", itemRequestId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "itemRequest с id " + itemRequestId + " не найден");
        }

        ItemRequestOutputDto itemRequestOutputDto = ItemRequestMapper.toOutputDto(itemRequestRepository.findById(itemRequestId).get());
        itemRequestOutputDto.setItems(ItemMapperDto.toListItemDto(itemRepository.findAllByRequestId(itemRequestId)));
        return itemRequestOutputDto;
    }

    @Override
    public List<ItemRequestOutputDto> getItemRequestsByOtherUsers(Long userId, Integer from, Integer size) {
        if (!userRepository.existsById(userId)) {
            log.info("user с id {} не найден", userId);
            throw new EntityNotFoundException("Юзер с id " + userId + " не найден");
        }

        Pageable sortedPagination = Utility.paginationWithoutSort(from, size);
        Page<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorIdNot(userId, sortedPagination);
        List<ItemRequestOutputDto> itemRequestOutputDtoList = ItemRequestMapper.toItemRequestOutputList(itemRequests.toList());
        for (ItemRequestOutputDto dto : itemRequestOutputDtoList) {
            dto.setItems(ItemMapperDto.toListItemDto(itemRepository.findAllByRequestId(dto.getId())));
        }
        return itemRequestOutputDtoList;
    }

    @Override
    public List<ItemRequestOutputDto> getItemRequest(Long userId) {
        if (!userRepository.existsById(userId)) {
            log.info("user с id {} не найден", userId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Юзер с id " + userId + " не найден");
        }
        List<ItemRequestOutputDto> list = ItemRequestMapper.toItemRequestOutputList(itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(userId));

        for (ItemRequestOutputDto request : list) {
            request.setItems(ItemMapperDto.toListItemDto(itemRepository.findAllByRequestId(request.getId())));
        }
        log.info("Получен список запросов user-а с id {}", userId);
        return list;
    }
}