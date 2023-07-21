package ru.practicum.shareit.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RequestMapping("/users")
@RestController
@Slf4j
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserDto> create(@RequestBody @Valid UserDto userDto){
        log.debug("Получен метод POST с id {} для создания юзера", userDto);
        return new ResponseEntity<>(userService.create(userDto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        log.debug("Получен метод delete для удаления юзера с id {} ", id);
        userService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        log.debug("Получен метод getId с id {} для получения юзера по id", id);
        return new ResponseEntity<>(userService.read(id), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers(){
        log.debug("Получен метод getAllUsers для получения всех юзеров");
        return new ResponseEntity<>(userService.readAll(), HttpStatus.OK);
    }


    @PatchMapping("/{id}")
    public ResponseEntity<UserDto> updateUser (@PathVariable Long id, @RequestBody UserDto user){
        log.debug("Поступил запрос updateUser для юзера с id {} на изменение", id);
        return new ResponseEntity<>(userService.update(user, id), HttpStatus.OK);
    }



}
