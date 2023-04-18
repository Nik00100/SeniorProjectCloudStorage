package ru.kirillov.seniorproject_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.kirillov.seniorproject_backend.dto.UserDto;
import ru.kirillov.seniorproject_backend.entity.UserEntity;
import ru.kirillov.seniorproject_backend.enums.Role;
import ru.kirillov.seniorproject_backend.exception.IncorrectDataEntry;
import ru.kirillov.seniorproject_backend.exception.UserNotFoundException;
import ru.kirillov.seniorproject_backend.repository.UserRepository;
import ru.kirillov.seniorproject_backend.utils.MapperUtils;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final UserRepository userRepository;
    private final MapperUtils mapperUtils;
    private final PasswordEncoder passwordEncoder;

    public UserDto registerUser(UserDto userDto) {
        UserEntity userEntity = mapperUtils.toUserEntity(userDto);

        log.info("Проверка свободности логина: {}", userEntity);
        userRepository.findUserByLogin(userEntity.getLogin()).ifPresent(s -> {
            log.info("Пользователь с таким логином: {} уже существует", userEntity.getLogin());
            throw new IncorrectDataEntry("Пользователь с таким логином: { " + userEntity.getLogin() +
                    " } уже зарегистрирован", userEntity.getId());
        });
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        userEntity.setRoles(Collections.singleton(Role.ROLE_USER));
        userEntity.setRole(Role.ROLE_USER.getAuthority());
        log.info("Новый пользователь зарегистрирован: {}", userEntity);
        return mapperUtils.toUserDto(userRepository.save(userEntity));
    }

    public UserDto getUser(Long id) {
        log.info("Поиск пользователя в базе данных по Id: {}", id);
        UserEntity userFound = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("Пользователь не найден", id));
        log.info("Пользователь в базе данных найден. ID: {}, логин: {}", id, userFound);
        return mapperUtils.toUserDto(userFound);
    }

    public void deleteUser(Long id) {
        log.info("Поиск пользователя в базе данных по Id: {}", id);
        UserEntity userFound = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("Пользователь не найден", id));
        log.info("Пользователь в базе данных найден. ID: {}, логин: {}", id, userFound);
        log.info("Пользователь удален. ID: {}, логин: {}", id, userFound);
        userRepository.deleteById(id);
    }
}