package ru.kirillov.seniorprojectcloudstorage.controller;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import ru.kirillov.seniorproject_backend.SeniorProjectCloudStorageApplication;
import ru.kirillov.seniorproject_backend.controller.RegistrationController;
import ru.kirillov.seniorproject_backend.dto.UserDto;
import ru.kirillov.seniorproject_backend.service.RegistrationService;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SeniorProjectCloudStorageApplication.class)
@AutoConfigureMockMvc
public class RegistrationControllerTest {

    private static final String AUTH_TOKEN = "auth-token";
    private static final String VALUE_TOKEN = "Bearer auth-token";
    private static final String LOGIN = "LoginTest";
    private static final String PASSWORD = "PasswordTest";

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private RegistrationService registrationService;

    @BeforeEach
    void setUp() {
        registrationService = mock(RegistrationService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new RegistrationController(registrationService)).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void test_registerUser() throws Exception {
        UserDto userDto = UserDto.builder()
                .login(LOGIN).password(PASSWORD).build();

        Mockito.when(registrationService.registerUser(userDto)).thenReturn(userDto);

        mockMvc.perform(post("/user/register")
                        .header(AUTH_TOKEN, VALUE_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(userDto)));
    }

    @Test
    void test_getUser() throws Exception {
        UserDto userDto = UserDto.builder()
                .login(LOGIN).password(PASSWORD).build();
        Mockito.when(registrationService.getUser(2L)).thenReturn(userDto);

        mockMvc.perform(get("/user/{id}", "2")
                        .header(AUTH_TOKEN, VALUE_TOKEN))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(userDto)));
    }

    @Test
    void test_deleteUser() throws Exception {
        mockMvc.perform(delete("/user/delete/{id}", "2")
                        .header(AUTH_TOKEN, VALUE_TOKEN))
                .andExpect(status().isOk());
    }
}
