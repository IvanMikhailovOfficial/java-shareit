package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping(path = "/{userId}")
    public ResponseEntity<UserDto> readUserByID(@PathVariable Long userId) {
        log.info("Получен POST запрос на получение  user по id {} ", userId);
        return new ResponseEntity<>(userService.getById(userId), HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<List<UserDto>> getAllUser() {
        log.info("Получен GET запрос на получение всех USERS");
        return new ResponseEntity<>(userService.getAll(), HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<UserDto> createUser(@RequestBody  UserDto userDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        log.info("Получен POST запрос на создание User");
        return new ResponseEntity<>(userService.save(userDto), HttpStatus.CREATED);
    }

    @PatchMapping(path = "/{userId}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long userId,
                                              @RequestBody UserDto userDto) {
        log.info("Получен PATCH запрос на обновление User c ID {} ", userId);
        return new ResponseEntity<>(userService.update(userId, userDto), HttpStatus.OK);
    }

    @DeleteMapping(path = "/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.delete(userId);
        log.info("Получен DELETE запрос на удаление USER с ID {}", userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}