package ru.kirillov.seniorprojectcloudstorage.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.kirillov.seniorproject_backend.SeniorProjectCloudStorageApplication;
import ru.kirillov.seniorproject_backend.dto.UserDto;
import ru.kirillov.seniorproject_backend.entity.UserEntity;
import ru.kirillov.seniorproject_backend.enums.Role;
import ru.kirillov.seniorproject_backend.repository.UserRepository;
import ru.kirillov.seniorproject_backend.service.RegistrationService;
import ru.kirillov.seniorproject_backend.utils.MapperUtils;

import java.util.Collections;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SeniorProjectCloudStorageApplication.class)
public class RegistrationServiceTest {

    @Autowired
    private RegistrationService registrationService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private MapperUtils mapperUtils;

    private UserEntity userEntity;
    private UserDto userDto;

    @BeforeEach
    public void init() {
        userDto = UserDto.builder()
                .login("LoginTest")
                .password("PasswordTest")
                .build();
        userEntity = UserEntity.builder()
                .id(1L)
                .login("LoginTest")
                .password("PasswordTest")
                .roles(Collections.singleton(Role.ROLE_USER))
                .build();
    }

    @Test
    public void test_registerUser() {
        Mockito.when(mapperUtils.toUserEntity(userDto)).thenReturn(userEntity);
        Mockito.when(userRepository.findUserByLogin(userEntity.getLogin())).thenReturn(Optional.empty());

        registrationService.registerUser(userDto);

        Mockito.verify(userRepository, Mockito.times(1)).findUserByLogin("LoginTest");
        Mockito.verify(userRepository, Mockito.times(1)).save(userEntity);
    }

    @Test
    public void test_getUser() {

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(userEntity));

        registrationService.getUser(1L);

        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    public void test_deleteUser() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(userEntity));

        registrationService.deleteUser(1L);

        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(userRepository, Mockito.times(1)).deleteById(1L);
    }
}
