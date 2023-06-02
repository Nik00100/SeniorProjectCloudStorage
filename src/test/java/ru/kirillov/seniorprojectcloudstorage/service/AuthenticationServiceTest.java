package ru.kirillov.seniorprojectcloudstorage.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.kirillov.seniorproject_backend.SeniorProjectCloudStorageApplication;
import ru.kirillov.seniorproject_backend.dto.UserDto;
import ru.kirillov.seniorproject_backend.entity.UserEntity;
import ru.kirillov.seniorproject_backend.model.Token;
import ru.kirillov.seniorproject_backend.repository.UserRepository;
import ru.kirillov.seniorproject_backend.security.JWTProvider;
import ru.kirillov.seniorproject_backend.service.AuthenticationService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SeniorProjectCloudStorageApplication.class)
@ContextConfiguration
public class AuthenticationServiceTest {

    private static final String AUTH_TOKEN = "auth-token";
    private static final String VALUE_TOKEN = "Bearer token";
    @Autowired
    private AuthenticationService authenticationService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private JWTProvider jwtProvider;
    @MockBean
    private PasswordEncoder passwordEncoder;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private UserEntity userEntity;
    private UserDto userDto;

    @BeforeEach
    public void init() {
        request = Mockito.mock(HttpServletRequest.class);
        response = Mockito.mock(HttpServletResponse.class);

        userDto = UserDto.builder()
                .login("LoginTest")
                .password("PasswordTest")
                .build();
        userEntity = UserEntity.builder()
                .id(1L)
                .login("LoginTest")
                .password("PasswordTest")
                .build();
    }

    @Test
    public void test_authenticationLogin() {
        Mockito.when(userRepository.findUserByLogin(userDto.getLogin())).thenReturn(Optional.ofNullable(userEntity));
        Mockito.when(passwordEncoder.matches(userDto.getPassword(), userEntity.getPassword())).thenReturn(true);
        Mockito.when(jwtProvider.generateToken(userEntity)).thenReturn(VALUE_TOKEN);

        Token generatedToken = authenticationService.authenticationLogin(userDto);

        Assertions.assertNotNull(generatedToken);
    }

    @Test
    @WithMockUser(username = "LoginTest",password = "PasswordTest")
    public void test_logout() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Mockito.when(userRepository.findUserByLogin(auth.getPrincipal().toString())).thenReturn(Optional.ofNullable(userEntity));

        String login = authenticationService.logout(AUTH_TOKEN, request, response);

        Assertions.assertEquals(login, userDto.getLogin());
    }
}
