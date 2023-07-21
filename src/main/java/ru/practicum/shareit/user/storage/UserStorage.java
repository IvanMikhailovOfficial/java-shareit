package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {

    User create(User user);
    User update(User user, Long id);
    void delete(Long id);
    User get(Long id);
    List<User> getAll();

    void userExist(Long userId);
}
