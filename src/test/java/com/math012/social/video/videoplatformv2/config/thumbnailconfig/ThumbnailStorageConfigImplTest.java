package com.math012.social.video.videoplatformv2.config.thumbnailconfig;

import com.math012.social.video.videoplatformv2.exception.PhotoProfileException;
import com.math012.social.video.videoplatformv2.exception.ThumbnailException;
import com.math012.social.video.videoplatformv2.exception.UserNotFoundException;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ThumbnailStorageConfigImplTest {

    @Autowired
    ThumbnailStorageConfigImpl thumbnailStorageConfig;

    @MockBean
    UserRepository userRepository;

    final String USERNAME = "bobLau";
    private static final MultipartFile FILE = new MockMultipartFile("file","filename.png","contentType",new byte[0]);
    private static final MultipartFile FILE_INVALID_MP4 = new MockMultipartFile("file","filename.mp4","contentType",new byte[0]);
    private static final MultipartFile fileInvalid = new MockMultipartFile("file","filename.mp4",null,new byte[0]);

    @BeforeEach
    void setup(){
        var user = new UserModel("Bob","Lau",USERNAME,"12345");
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
    }

    @Test
    void saveFileSuccess(){
        String response = thumbnailStorageConfig.saveFile(FILE, USERNAME);
        assertEquals(FILE.getOriginalFilename(),response);
    }

    @Test
    void saveFileFailedWhenTheUsernameIsNull(){
        Exception exception = assertThrows(UserNotFoundException.class, () ->{
            thumbnailStorageConfig.saveFile(FILE, null);
        });
        assertEquals("This username: null, not found", exception.getMessage());
    }

    @Test
    void saveFileFailedWhenTheUsernameIsNotRegistered(){
        Exception exception = assertThrows(UserNotFoundException.class, () ->{
            thumbnailStorageConfig.saveFile(FILE, "Adam");
        });
        assertEquals("This username: Adam, not found", exception.getMessage());
    }

    @Test
    void saveFileFailedWhenTheFormatIsMp4(){
        Exception exception = assertThrows(ThumbnailException.class, () ->{
            thumbnailStorageConfig.saveFile(FILE_INVALID_MP4, USERNAME);
        });
        assertEquals("Video upload is not supported for thumbnails", exception.getMessage());
    }

    @Test
    void saveFileFailedWhenTheFileIsNull(){
        Exception exception = assertThrows(ThumbnailException.class, () ->{
            thumbnailStorageConfig.saveFile(null,USERNAME);
        });
        assertEquals("The thumbnail is null, please try again", exception.getMessage());
    }

    @Test
    void loadFileSuccess(){
        String response = thumbnailStorageConfig.loadFile(FILE.getOriginalFilename(),USERNAME).toString();
        assertEquals("URL [file:///C:/videos/bobLau/thumbnail/filename.png]",response);
    }

    @Test
    void loadFilesFailedWhenThePathIsNull(){
        Exception exception = assertThrows(ThumbnailException.class, () ->{
            thumbnailStorageConfig.loadFile(null,USERNAME);
        });
        assertEquals("Download thumbnail URL was not found", exception.getMessage());
    }

    @Test
    void loadFilesFailedWhenTheUsernameIsNull(){
        Exception exception = assertThrows(UserNotFoundException.class, () ->{
            thumbnailStorageConfig.loadFile("path",null);
        });
        assertEquals("username invalid, try again",exception.getMessage());
    }
}
