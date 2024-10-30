package com.math012.social.video.videoplatformv2.config.videoconfig;
import static org.junit.jupiter.api.Assertions.*;

import com.math012.social.video.videoplatformv2.exception.LoadVideoException;
import com.math012.social.video.videoplatformv2.exception.UserNotFoundException;
import com.math012.social.video.videoplatformv2.exception.VideoStorageException;
import com.math012.social.video.videoplatformv2.model.UserModel;
import com.math012.social.video.videoplatformv2.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.mockito.Mockito.when;

@SpringBootTest
public class VideoStorageConfigImplTest {

    @Autowired
    VideoStorageConfigImpl videoStorageConfig;

    @MockBean
    UserRepository userRepository;

    private static final String USERNAME = "bobLau";
    private static final MultipartFile file = new MockMultipartFile("file","filename.mp4","contentType",new byte[0]);
    private static final MultipartFile fileInvalidMp4 = new MockMultipartFile("file","filename","contentType",new byte[0]);
    private static final MultipartFile fileInvalid = new MockMultipartFile("file","filename.mp4",null,new byte[0]);

    @BeforeEach
    void setup(){
        var user = new UserModel("Bob","Lau",USERNAME,"12345");
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
    }

    @Test
    void saveFileSuccess(){
        var response = videoStorageConfig.saveFile(file, USERNAME);
        assertEquals(file.getOriginalFilename(),response);
    }

    @Test
    void saveFileFailedWhenTheUsernameIsNull(){
        Exception exception = assertThrows(UserNotFoundException.class, () ->{
            videoStorageConfig.saveFile(file, null);
        });
        assertEquals("This username: null, not found", exception.getMessage());
    }

    @Test
    void saveFileFailedWhenTheUsernameIsNotRegistered(){
        Exception exception = assertThrows(UserNotFoundException.class, () ->{
            videoStorageConfig.saveFile(file, "Adam");
        });
        assertEquals("This username: Adam, not found", exception.getMessage());
    }

    @Test
    void saveFileFailedWhenTheFormatIsNotMp4(){
        Exception exception = assertThrows(VideoStorageException.class, () ->{
            videoStorageConfig.saveFile(fileInvalidMp4, USERNAME);
        });
        assertEquals("Please, send mp4 format only", exception.getMessage());
    }

    @Test
    void saveFileFailedWhenTheFileIsNull(){
        Exception exception = assertThrows(VideoStorageException.class, () ->{
            videoStorageConfig.saveFile(null,USERNAME);
        });
        assertEquals("The video was not saved, please try again", exception.getMessage());
    }

    @Test
    void loadFileSuccess(){
        String response = videoStorageConfig.loadFile(file.getOriginalFilename(),USERNAME).toString();
        assertEquals("URL [file:///C:/videos/bobLau/filename.mp4]",response);
    }

    @Test
    void loadFilesFailedWhenThePathIsNull(){
        Exception exception = assertThrows(LoadVideoException.class, () ->{
            videoStorageConfig.loadFile(null,USERNAME);
        });
        assertEquals("Download URL was not found", exception.getMessage());
    }

    @Test
    void loadFilesFailedWhenTheUsernameIsNull(){
        Exception exception = assertThrows(UserNotFoundException.class, () ->{
            videoStorageConfig.loadFile("path",null);
        });
        assertEquals("username invalid, try again",exception.getMessage());
    }
}
