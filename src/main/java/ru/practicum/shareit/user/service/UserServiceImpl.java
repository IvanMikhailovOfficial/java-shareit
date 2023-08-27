package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.exceptions.exp.EmailDuplicateException;
import ru.practicum.shareit.exceptions.exp.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapperDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

import static ru.practicum.shareit.user.dto.UserMapperDto.toListUserDto;
import static ru.practicum.shareit.user.dto.UserMapperDto.toUserDto;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto save(UserDto userDto) {
        User user = UserMapperDto.fromUserDto(userDto);

        if (userRepository.existsUserByEmailIs(userDto.getEmail())) {
            userRepository.save(user);
            log.info("Пользователь с таким email уже существует");
            throw new EmailDuplicateException("Пользователь с таким email уже существует");
        }


        log.info("Пользователь сохранен: " + userDto);

        return toUserDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserDto update(Long id, UserDto userDto) {
        User user = userRepository.findById(id).get();

        if (userDto.getEmail() != null) {
            if (userRepository.existsUserByEmailIs(userDto.getEmail()) &&
                    !user.getEmail().equals(userDto.getEmail())) {
                log.info("Пользователь с таким email уже существует");
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Пользователь с таким email уже существует");
            }
            user.setEmail(userDto.getEmail());
        }

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        log.info("Пользователь обновлён: " + userDto);

        return toUserDto(userRepository.save(user));

    }

    @Override
    public UserDto getById(Long id) {
        Optional<User> user = userRepository.findById(id);

        if (user.isPresent()) {
            log.info("User c id {} успешно получен", id);
            return toUserDto(user.get());
        }
        log.info("Пользователь с ID {} не найден ", id);
        throw new EntityNotFoundException("Пользователь не найден");
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (userRepository.existsUserByIdIs(id)) {
            userRepository.deleteUserByIdIs(id);
            log.info("User {} успешно удален", id);
            return;
        }
        log.info("User с id {} невозможно удалить,так как его не существует ", id);
        throw new EntityNotFoundException("Пользователь не найден");

    }

    @Override
    public List<UserDto> getAll() {
        List<User> allUsers = userRepository.findAll();
        log.info("Все Users успешно получены");
        return toListUserDto(allUsers);
    }
}
