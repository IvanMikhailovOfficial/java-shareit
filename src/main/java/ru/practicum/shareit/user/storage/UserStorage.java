package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    Optional<User> save(UserDto userDto);

    Optional<User> patch(Long id, UserDto userDto);

    Optional<User> findById(Long id);

    void delete(Long id);

    List<User> findAll();
}
