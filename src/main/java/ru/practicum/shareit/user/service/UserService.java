package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto create(UserDto userDto);
    UserDto update(UserDto userDto, Long id);
    void delete(Long id);
    List<UserDto> readAll();
    UserDto read(Long id);
}
