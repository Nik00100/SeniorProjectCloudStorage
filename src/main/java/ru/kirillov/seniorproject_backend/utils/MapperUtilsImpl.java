package ru.kirillov.seniorproject_backend.utils;

import org.springframework.stereotype.Service;
import ru.kirillov.seniorproject_backend.dto.UserDto;
import ru.kirillov.seniorproject_backend.entity.UserEntity;

@Service
public class MapperUtilsImpl implements MapperUtils {

    @Override
    public UserEntity toUserEntity(UserDto userDto) {
        UserEntity userEntity = new UserEntity();
        userEntity.setLogin(userDto.getLogin());
        userEntity.setPassword(userDto.getPassword());
        return userEntity;
    }

    @Override
    public UserDto toUserDto(UserEntity userEntity) {
        UserDto userDto = new UserDto();
        userDto.setLogin(userEntity.getLogin());
        userDto.setPassword(userEntity.getPassword());
        return userDto;
    }
}
