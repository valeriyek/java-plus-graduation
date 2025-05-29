package ru.practicum.user.model;


import org.mapstruct.Mapper;
import ru.practicum.dto.UserDto;
import ru.practicum.dto.UserShortDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toUserDto(User user);

    UserShortDto toUserShortDto(User user);

    List<UserDto> toUserDtoList(List<User> users);

    User toUser(UserDto userDto);

    List<UserShortDto> toUserShortDto(List<User> users);
}