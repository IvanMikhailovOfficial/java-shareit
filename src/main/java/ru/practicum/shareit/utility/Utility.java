package ru.practicum.shareit.utility;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
public class Utility {

    private Utility() {
    }

    public static Pageable paginationWithSort(Integer from, Integer size) {

        if (from == null || size == null) {
            return PageRequest.of(0, Integer.MAX_VALUE, Sort.by("created").descending());
        } else if (from < 0 || size <= 0) {
            log.info("Ошибка пагинации, поступили значения from = {} и size = {}", from, size);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Некорректные значения для создания пагинации");
        }
        return PageRequest.of(from / size, size, Sort.by("created").descending());
    }

    public static Pageable paginationWithoutSort(Integer from, Integer size) {
        if (from == null || size == null) {
            return PageRequest.of(0, Integer.MAX_VALUE);
        } else if (from < 0 || size <= 0) {
            log.info("Ошибка пагинации, поступили значения from = {} и size = {}", from, size);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Некорректные значения для создания пагинации");
        }
        return PageRequest.of(from / size, size);
    }
}