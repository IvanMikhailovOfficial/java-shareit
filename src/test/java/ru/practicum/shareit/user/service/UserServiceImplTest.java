package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.exceptions.exp.EmailDuplicateException;
import ru.practicum.shareit.exceptions.exp.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapperDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    private UserService userService;

    private UserDto userDto;
    private User user;

    @BeforeEach
    void init() {
        userService = new UserServiceImpl(userRepository);
        userDto = UserDto.builder()
                .id(1L)
                .name("1")
                .email("1@mail.com")
                .build();

        user = User.builder()
                .id(1L)
                .name("1")
                .email("1@mail.com")
                .build();
    }

    @Test
    @DisplayName("save 200 status")
    void save200status() {
        when(userRepository.save(any())).thenReturn(user);

        UserDto userSave = userService.save(userDto);

        assertEquals(user.getEmail(), userSave.getEmail());
        assertEquals(user.getName(), userSave.getName());
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("save email exist")
    void saveExistEmail() {
        when(userRepository.existsUserByEmailIs(user.getEmail())).thenThrow(EmailDuplicateException.class);

        assertThrows(EmailDuplicateException.class, () -> userService.save(userDto));

        verify(userRepository, never()).save(user);
        verify(userRepository, times(1)).existsUserByEmailIs(user.getEmail());
    }

    @Test
    @DisplayName("update name and email 200 status")
    void updateNameAndEmail() {
        User userUpdate = User.builder()
                .id(1L)
                .name("nameUpdate")
                .email("emailUpdate@mail.com")
                .build();

        when(userRepository.save(any())).thenReturn(userUpdate);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        UserDto userSave = userService.update(user.getId(), UserMapperDto.toUserDto(userUpdate));

        assertEquals(userUpdate.getEmail(), userSave.getEmail());
        assertEquals(userUpdate.getName(), userSave.getName());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("update (user не существует))")
    void updateUserNotExist() {
        User userUpdate = User.builder()
                .id(1L)
                .name("nameUpdated")
                .email("update@mail.com")
                .build();

        when(userRepository.findById(any())).thenReturn(Optional.empty());
        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> userService.update(userUpdate.getId(), UserMapperDto.toUserDto(userUpdate)));

        assertEquals("такого user не существует", entityNotFoundException.getMessage());
        verify(userRepository, times(0)).save(user);
        verify(userRepository, times(1)).findById(userUpdate.getId());
    }

    @Test
    @DisplayName("update дублирование email 400")
    void updatEmailExist() {
        User userUpdate = User.builder()
                .id(1L)
                .name("nameUpdated")
                .email("email11@mail.com")
                .build();

        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(userRepository.existsUserByEmailIs(userUpdate.getEmail())).thenThrow(ResponseStatusException.class);

        assertThrows(ResponseStatusException.class,
                () -> userService.update(userUpdate.getId(), UserMapperDto.toUserDto(userUpdate)));
        verify(userRepository, times(0)).save(user);
        verify(userRepository, times(1)).existsUserByEmailIs(userUpdate.getEmail());
    }

    @Test
    @DisplayName("getById успешное получение")
    void getById200status() {
        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        UserDto userDto = userService.getById(user.getId());

        assertEquals(user.getEmail(), userDto.getEmail());
        assertEquals(user.getName(), userDto.getName());
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    @DisplayName("getById несуществующий юзер")
    void getByIdUserNotExist() {
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> userService.getById(user.getId()));

        assertEquals("Пользователь не найден", entityNotFoundException.getMessage());
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    @DisplayName("delete успешное удалени")
    void delete200() {
        when(userRepository.existsUserByIdIs(any())).thenReturn(true);

        userService.delete(user.getId());

        verify(userRepository, times(1)).existsUserByIdIs(user.getId());
        verify(userRepository, times(1)).deleteUserByIdIs(user.getId());
    }

    @Test
    @DisplayName("delete user не сущестует")
    void deleteUserNotExist() {
        when(userRepository.existsUserByIdIs(eq(user.getId()))).thenReturn(false);

        EntityNotFoundException userNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> userService.delete(user.getId()));

        assertEquals("Пользователь не найден", userNotFoundException.getMessage());
        verify(userRepository, times(1)).existsUserByIdIs(user.getId());
        verify(userRepository, times(0)).deleteUserByIdIs(user.getId());
    }

    @Test
    @DisplayName("getAll 200 статус")
    void getAll200status() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserDto> userDtoList = userService.getAll();

        assertEquals(1, userDtoList.size());
    }

    @Test
    @DisplayName("getAll пустой список")
    void getAllListEmpty() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<UserDto> userDtoList = userService.getAll();

        assertEquals(0, userDtoList.size());
    }

    @Test
    void save_shouldThrowEmailDuplicateException_whenUserWithEmailAlreadyExists() {

        when(userRepository.existsUserByEmailIs(userDto.getEmail())).thenReturn(true);

        assertThrows(EmailDuplicateException.class, () -> {
            userService.save(userDto);
        });

        verify(userRepository, times(1)).existsUserByEmailIs(userDto.getEmail());
    }
}