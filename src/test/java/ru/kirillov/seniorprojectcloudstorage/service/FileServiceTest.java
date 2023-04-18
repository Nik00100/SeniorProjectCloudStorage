package ru.kirillov.seniorprojectcloudstorage.service;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;
import ru.kirillov.seniorproject_backend.dto.FileDto;
import ru.kirillov.seniorproject_backend.entity.FileEntity;
import ru.kirillov.seniorproject_backend.entity.UserEntity;
import ru.kirillov.seniorproject_backend.model.FileBody;
import ru.kirillov.seniorproject_backend.repository.FileRepository;
import ru.kirillov.seniorproject_backend.security.JWTToken;
import ru.kirillov.seniorproject_backend.service.FileService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class FileServiceTest {

    private static final String MY_FILE_NAME = "fileName.txt";
    @Autowired
    private FileService fileService;
    @MockBean
    private FileRepository fileRepository;
    @MockBean
    private JWTToken jwtToken;
    private UserEntity user;
    private FileEntity file;

    @BeforeEach
    public void init() {
        user = UserEntity.builder().id(1L).build();
        file = FileEntity.builder()
                .id(10L)
                .fileName(MY_FILE_NAME)
                .hash("856bc192834da78ac51d23be0d55a786")
                .fileType(MediaType.TEXT_PLAIN_VALUE)
                .size(13L)
                .fileData("Cloud_storage".getBytes())
                .userEntity(user)
                .build();
    }

    @Test
    void test_addFile() throws IOException {
        String hash = "856bc192834da78ac51d23be0d55a786";
        MultipartFile multipartFile =
                new MockMultipartFile("file", MY_FILE_NAME,
                        MediaType.TEXT_PLAIN_VALUE, "Cloud_storage".getBytes());
        Mockito.when(jwtToken.getAuthenticatedUser()).thenReturn(user);
        Mockito.when(fileRepository.findFileByUserEntityIdAndFileName(1L, MY_FILE_NAME))
                .thenReturn(Optional.empty());
        Mockito.when(fileRepository.findFileByUserEntityIdAndHash(1L, hash))
                .thenReturn(Optional.empty());
        FileEntity createdFile = FileEntity.builder()
                .id(10L)
                .hash(hash)
                .fileName(MY_FILE_NAME)
                .fileType(multipartFile.getContentType())
                .size(multipartFile.getSize())
                .fileData(multipartFile.getBytes())
                .userEntity(UserEntity.builder().id(1L).build())
                .build();

        fileService.addFile(multipartFile, MY_FILE_NAME);

        Assertions.assertEquals(13L, createdFile.getSize());
    }

    @Test
    void test_getFile() {

        Mockito.when(jwtToken.getAuthenticatedUser()).thenReturn(user);
        Mockito.when(fileRepository.findFileByUserEntityIdAndFileName(1L, MY_FILE_NAME))
                .thenReturn(Optional.ofNullable(file));

        FileDto downloadFile = fileService.getFile(MY_FILE_NAME);

        Assertions.assertEquals(MY_FILE_NAME, downloadFile.getFileName());
    }

    @Test
    void test_renameFile() {
        FileBody newName = new FileBody("newFileName.txt");
        Mockito.when(jwtToken.getAuthenticatedUser()).thenReturn(user);
        Mockito.when(fileRepository.findFileByUserEntityIdAndFileName(1L, MY_FILE_NAME))
                .thenReturn(Optional.ofNullable(file));

        fileService.renameFile(MY_FILE_NAME, newName);

        Mockito.verify(fileRepository, Mockito.times(1)).save(file);
    }

    @Test
    void test_deleteFile() {
        Mockito.when(jwtToken.getAuthenticatedUser()).thenReturn(user);
        Mockito.when(fileRepository.findFileByUserEntityIdAndFileName(1L, MY_FILE_NAME))
                .thenReturn(Optional.ofNullable(file));

        fileService.deleteFile(MY_FILE_NAME);

        Mockito.verify(fileRepository, Mockito.times(1)).deleteById(file.getId());
    }

    @Test
    void test_getAllFiles() {
        int limit = 3;
        List<FileEntity> listFile = List.of(
                FileEntity.builder().size(1111L).fileName("file1.txt").build(),
                FileEntity.builder().size(2222L).fileName("file2.txt").build(),
                FileEntity.builder().size(3333L).fileName("file3.txt").build());
        Mockito.when(jwtToken.getAuthenticatedUser()).thenReturn(user);
        Mockito.when(fileRepository.findFilesByUserIdWithLimit(user.getId(), limit)).thenReturn(listFile);

        List<FileDto> files = fileService.getAllFiles(limit);

        Assertions.assertEquals("file1.txt", files.get(0).getFileName());
    }
}
