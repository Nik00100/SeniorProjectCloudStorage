package ru.kirillov.seniorproject_backend.utils;


import ru.kirillov.seniorproject_backend.dto.UserDto;
import ru.kirillov.seniorproject_backend.entity.UserEntity;

public interface MapperUtils {

    UserEntity toUserEntity(UserDto userDto);

    UserDto toUserDto(UserEntity userEntity);

}
