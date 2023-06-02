package ru.kirillov.seniorprojectcloudstorage.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import ru.kirillov.seniorproject_backend.SeniorProjectCloudStorageApplication;
import ru.kirillov.seniorproject_backend.controller.FileController;
import ru.kirillov.seniorproject_backend.dto.FileDto;
import ru.kirillov.seniorproject_backend.model.FileBody;
import ru.kirillov.seniorproject_backend.service.FileService;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SeniorProjectCloudStorageApplication.class)
@AutoConfigureMockMvc
public class FileControllerTest {

    private static final String AUTH_TOKEN = "auth-token";
    private static final String VALUE_TOKEN = "Bearer auth-token";
    private static final String FILE_NAME = "filename";
    private static final String MY_FILE_NAME = "fileName.txt";
    private static final String URL_FILE = "/file";

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private FileService fileService;

    @BeforeEach
    void setUp() {
        fileService = mock(FileService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new FileController(fileService)).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void test_addFile() throws Exception {
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                MY_FILE_NAME,
                MediaType.TEXT_PLAIN_VALUE,
                "Cloud_storage".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart(URL_FILE)
                        .file(multipartFile)
                        .param(FILE_NAME, "file")
                        .header(AUTH_TOKEN, VALUE_TOKEN))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void test_getFile() throws Exception {
        FileDto fileDto = FileDto.builder()
                .fileName(MY_FILE_NAME)
                .fileData("Cloud_storage".getBytes())
                .fileType(MediaType.TEXT_PLAIN_VALUE).build();

        Mockito.when(fileService.getFile(MY_FILE_NAME)).thenReturn(fileDto);

        mockMvc.perform(get(URL_FILE)
                        .param(FILE_NAME, MY_FILE_NAME)
                        .header(AUTH_TOKEN, VALUE_TOKEN))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN_VALUE));
    }

    @Test
    void test_renameFile() throws Exception {
        FileBody newName = new FileBody("newFileName.txt");

        mockMvc.perform(put(URL_FILE)
                        .param(FILE_NAME, MY_FILE_NAME)
                        .header(AUTH_TOKEN, VALUE_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newName)))
                .andExpect(status().isOk());
    }

    @Test
    void test_deleteFile() throws Exception {
        mockMvc.perform(delete(URL_FILE)
                        .param(FILE_NAME, MY_FILE_NAME)
                        .header(AUTH_TOKEN, VALUE_TOKEN))
                .andExpect(status().isOk());
    }

    @Test
    void test_getAllFiles() throws Exception {
        List<FileDto> listFile = List.of(
                FileDto.builder().size(1111L).fileName("file1.txt").build(),
                FileDto.builder().size(2222L).fileName("file2.txt").build(),
                FileDto.builder().size(3333L).fileName("file3.txt").build());
        Mockito.when(fileService.getAllFiles(3)).thenReturn(listFile);

        mockMvc.perform(get("/list")
                        .header(AUTH_TOKEN, VALUE_TOKEN)
                        .param("limit", "3"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(listFile)));
    }
}
