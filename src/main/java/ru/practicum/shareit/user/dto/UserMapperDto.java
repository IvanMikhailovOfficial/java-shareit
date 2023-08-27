package ru.practicum.shareit.user.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class UserMapperDto {
    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User fromUserDtoWithId(Long id, UserDto userDto) {
        return User.builder()
                .id(id)
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }

    public static User fromUserDto(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }

    public static List<UserDto> toListUserDto(List<User> users) {
        return users.stream()
                .map(UserMapperDto::toUserDto)
                .collect(Collectors.toList());
    }
}
