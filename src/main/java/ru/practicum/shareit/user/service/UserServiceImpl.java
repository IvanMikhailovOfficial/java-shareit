package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserServiceImpl(UserStorage userStorage){
        this.userStorage = userStorage;
    }

    @Override
    public UserDto create(UserDto userDto) {
        return UserMapper.toUserDto(userStorage.create(UserMapper.toUser(userDto)));
    }

    @Override
    public UserDto update(UserDto userDto, Long id) {
        return UserMapper.toUserDto(userStorage.update(UserMapper.toUser(userDto), id));
    }

    @Override
    public void delete(Long id) {
        userStorage.delete(id);
    }

    @Override
    public List<UserDto> readAll() {
        return UserMapper.toListUserDto(userStorage.getAll());
    }

    @Override
    public UserDto read(Long id) {
        return UserMapper.toUserDto(userStorage.get(id));
    }
}
