package ru.practicum.shareit.user.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.exp.EmailDuplicateException;
import ru.practicum.shareit.exceptions.exp.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapperDto;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserStorageImpl implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private final List<String> emails = new ArrayList<>();
    private static Long index = 1L;

    @Override
    public Optional<User> save(UserDto userDto) {
        userDto.setId(index);

        checkEmailUnique(userDto);

        users.put(userDto.getId(), UserMapperDto.fromUserDtoWithId(userDto.getId(), userDto));
        emails.add(userDto.getEmail());
        increasedId();

        return Optional.of(UserMapperDto.fromUserDtoWithId(userDto.getId(), userDto));
    }

    private static void increasedId() {
        index++;
    }

    private void checkEmailUnique(UserDto userDto) {
        if (emails.contains(userDto.getEmail())) {
            throw new EmailDuplicateException("Пользователь с таким email уже существует");
        }
    }

    @Override
    public Optional<User> patch(Long id, UserDto userDto) {
        if (users.containsKey(id)) {
            User userUpdate = users.get(id);

            if (userDto.getName() != null) {
                userUpdate.setName(userDto.getName());
            }

            if (userDto.getEmail() != null) {
                if (emails.contains(userDto.getEmail()) && !userUpdate.getEmail().equals(userDto.getEmail())) {
                    throw new EmailDuplicateException("Пользователь с таким email уже существует");
                }
                emails.remove(userUpdate.getEmail());
                userUpdate.setEmail(userDto.getEmail());
            }

            users.put(id, userUpdate);
            return Optional.of(users.get(id));
        }

        return Optional.empty();
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void delete(Long id) {
        if (!users.containsKey(id)) {
            throw new EntityNotFoundException("Пользователь не найден");
        }

        emails.remove(users.get(id).getEmail());
        users.remove(id);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }
}
