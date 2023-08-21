package ru.practicum.shareit.utility;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UtilityTest {
    @Test
    void getPaginationWithSortDesc_whenPaginationNull_ThenReturnPage0() {
        Pageable paginationWithSortDesc = Utility.paginationWithSort(null, null);

        assertEquals(0, paginationWithSortDesc.getPageNumber());
        assertEquals(Integer.MAX_VALUE, paginationWithSortDesc.getPageSize());
    }

    @Test
    void getPaginationWithSortDesc_whenPaginationFrom0Size1_ThenReturnPage0() {
        Pageable paginationWithSortDesc = Utility.paginationWithSort(0, 1);

        assertEquals(0, paginationWithSortDesc.getPageNumber());
        assertEquals(1, paginationWithSortDesc.getPageSize());
    }

    @Test
    void getPaginationWithSortDesc_whenPaginationNegative_ThenThrowPaginationParameterException() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            Utility.paginationWithSort(-1, -1);
        });

        assertEquals("400 BAD_REQUEST \"Некорректные значения для создания пагинации\"", exception.getMessage());
    }

    @Test
    void getPaginationWithoutSort_whenPaginationFrom0Size1_ThenReturnPage0() {
        Pageable paginationWithoutSort = Utility.paginationWithoutSort(0, 1);

        assertEquals(0, paginationWithoutSort.getPageNumber());
        assertEquals(1, paginationWithoutSort.getPageSize());
    }

    @Test
    void getPaginationWithoutSort_whenPaginationNull_ThenReturnPage0() {
        Pageable paginationWithoutSort = Utility.paginationWithoutSort(null, null);

        assertEquals(0, paginationWithoutSort.getPageNumber());
        assertEquals(Integer.MAX_VALUE, paginationWithoutSort.getPageSize());
    }

    @Test
    void getPaginationWithoutSort_whenPaginationNegative_ThenThrowPaginationParameterException() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> Utility.paginationWithoutSort(-1, -1));

        assertEquals("400 BAD_REQUEST \"Некорректные значения для создания пагинации\"", exception.getMessage());
    }
}