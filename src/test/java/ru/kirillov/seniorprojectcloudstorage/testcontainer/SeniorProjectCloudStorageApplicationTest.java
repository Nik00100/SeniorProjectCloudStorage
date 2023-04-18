package ru.kirillov.seniorprojectcloudstorage.testcontainer;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.kirillov.seniorproject_backend.SeniorProjectCloudStorageApplication;
import ru.kirillov.seniorproject_backend.dto.UserDto;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

@Testcontainers
@SpringBootTest(classes = SeniorProjectCloudStorageApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SeniorProjectCloudStorageApplicationTest {

    private static final int PORT = 8888;
    private static final String LOGIN = "LoginTest";
    private static final String PASSWORD = "PasswordTest";

    @Autowired
    public TestRestTemplate restTemplate;
    private UserDto userDto;

    @Container
    public static MySQLContainer mySQLContainer = MySQLTestContainer.getInstance();

    @Container
    public static GenericContainer<?> appBackendCloud =
            new GenericContainer<>("app-cloud-storage-back:latest")
                    .withExposedPorts(PORT)
                    .dependsOn(mySQLContainer);
    @BeforeEach
    public void init()
    {
        userDto = UserDto.builder()
                .login(LOGIN)
                .password(PASSWORD)
                .build();
    }

    @Test
    void registerUserTest() throws URISyntaxException {
        System.out.println(userDto);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        URI url = new URI("http://" + appBackendCloud.getHost() + ":" + PORT + "/user/register");
        HttpEntity<UserDto> requestEntity = new HttpEntity<>(userDto, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<UserDto> responseEntity = restTemplate.postForEntity(url, requestEntity, UserDto.class);

        System.out.println("Status Code: " + responseEntity.getStatusCode());
        Assertions.assertEquals(LOGIN, Objects.requireNonNull(responseEntity.getBody()).getLogin());
    }

    @Test
    void loginAppTest() {
        String getLoginURI = "http://" + appBackendCloud.getHost() + ":" + PORT + "/login";
        String authToken = restTemplate.postForObject(getLoginURI, userDto, String.class);

        System.out.println(authToken);
        Assertions.assertNotNull(authToken);
    }
}
