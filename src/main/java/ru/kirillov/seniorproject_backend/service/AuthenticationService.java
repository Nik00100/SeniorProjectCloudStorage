package ru.kirillov.seniorproject_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import ru.kirillov.seniorproject_backend.dto.UserDto;
import ru.kirillov.seniorproject_backend.entity.UserEntity;
import ru.kirillov.seniorproject_backend.exception.IncorrectDataEntry;
import ru.kirillov.seniorproject_backend.exception.UserNotFoundException;
import ru.kirillov.seniorproject_backend.model.Token;
import ru.kirillov.seniorproject_backend.repository.UserRepository;
import ru.kirillov.seniorproject_backend.security.JWTProvider;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final JWTProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    public Token authenticationLogin(UserDto userDto) {
        log.info("Поиск пользователя в базе данных по логину: {}", userDto.getLogin());
        final UserEntity userFromDatabase = userRepository.findUserByLogin(userDto.getLogin()).orElseThrow(()
                -> new UserNotFoundException("Пользователь не найден", 0));
        if (passwordEncoder.matches(userDto.getPassword(), userFromDatabase.getPassword())) {
            final String token = jwtProvider.generateToken(userFromDatabase);
            return new Token(token);
        } else {
            throw new IncorrectDataEntry("Неправильный пароль", 0);
        }
    }

    public String logout(String authToken, HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserEntity userEntity = userRepository.findUserByLogin(auth.getPrincipal().toString()).orElseThrow(()
                -> new UserNotFoundException("Пользователь не найден", 0));
        log.info("Пользователь начал процедуру выхода из системы: {}", userEntity);
        SecurityContextLogoutHandler securityContextLogoutHandler = new SecurityContextLogoutHandler();
        if (userEntity == null) {
            return null;
        }
        securityContextLogoutHandler.logout(request, response, auth);
        jwtProvider.removeToken(authToken);
        log.info("Токен пользователя: {} удален из списка активных токеннов. Auth-token: {}", userEntity, authToken);
        return userEntity.getLogin();
    }
}
