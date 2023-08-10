package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto save(UserDto userDto);

    UserDto update(Long id, UserDto userDto);

    UserDto getById(Long id);

    void delete(Long id);

    List<UserDto> getAll();
}
