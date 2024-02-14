package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.service.FileService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FileControllerTest {
    private final FileService fileService = mock(FileService.class);

    private final FileController fileController = new FileController(fileService);

    private final FileDto testFile = new FileDto("testFile.img",
            new byte[] {1, 2, 3});

    @Test
    public void whenGetFileByIdThenResponseWithFileContent() {
        when(fileService.getFileById(anyInt())).thenReturn(Optional.of(testFile));
        var response = fileController.getById(1);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(testFile.getContent());
    }

    @Test
    public void whenCannotGetFileByIdThenNotFoundResponse() {
        when(fileService.getFileById(anyInt())).thenReturn(Optional.empty());
        var response = fileController.getById(1);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
