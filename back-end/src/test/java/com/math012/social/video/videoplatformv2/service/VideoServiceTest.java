package com.math012.social.video.videoplatformv2.service;

import com.math012.social.video.videoplatformv2.exception.InvalidFieldsException;
import com.math012.social.video.videoplatformv2.exception.UserNotFoundException;
import com.math012.social.video.videoplatformv2.exception.VideoNotFoundException;
import com.math012.social.video.videoplatformv2.model.UserModel;
import com.math012.social.video.videoplatformv2.model.VideoModel;
import com.math012.social.video.videoplatformv2.repository.UserRepository;
import com.math012.social.video.videoplatformv2.repository.VideoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class VideoServiceTest {

    @Autowired
    VideoService videoService;

    @MockBean
    VideoRepository videoRepository;

    @MockBean
    UserRepository userRepository;

    private static final UUID USER_UUID = UUID.randomUUID();
    private static final UUID VIDEO_UUID = UUID.randomUUID();
    private static final UUID VIDEO_UUID_INVALID = UUID.randomUUID();
    private static final UserModel USER = new UserModel("Bob", "Lau", "BobLau","12345");
    private static final VideoModel VIDEO = new VideoModel("title","description","video url",USER, Instant.now());
    private static final Pageable PAGEABLE = PageRequest.of(0,1);
    private static final String USERNAME = "BobLau";

    @BeforeEach
    void setup(){
        List<VideoModel> videos = List.of(new VideoModel("title","description","video url",USER, Instant.now()),
                new VideoModel("title 2","description 2","video url 2",USER, Instant.now()),
                new VideoModel("title 3","description 3","video url 3",USER, Instant.now()));
        Page<VideoModel> pageVideo = new PageImpl<>(videos);
        // FIND ALL
        when(videoRepository.findAll()).thenReturn(videos);
        // FIND ALL WITH PAGEABLE
        when(videoRepository.findAll(PAGEABLE)).thenReturn(pageVideo);
        // FIND BY ID
        when(videoRepository.findById(VIDEO_UUID)).thenReturn(Optional.of(VIDEO));
        // FIND BY USERNAME
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(USER));
        // FIND ALL VIDEOS BY USERNAME
        when(videoRepository.findAllVideosByUsername(USER)).thenReturn(videos);
        // SAVE VIDEO
        when(videoRepository.save(VIDEO)).thenReturn(VIDEO);
        // DELETE VIDEO BY ID
        doNothing().when(videoRepository).deleteById(VIDEO_UUID);
        // FIND USERNAME BY VIDEO ID
        when(videoRepository.findUsernameByVideoId(VIDEO_UUID)).thenReturn(VIDEO_UUID);
        when(userRepository.findUsernameById(VIDEO_UUID)).thenReturn(String.valueOf(VIDEO_UUID));
        when(videoRepository.findUsernameByVideoId(VIDEO_UUID_INVALID)).thenThrow(new VideoNotFoundException("The UUID of video is not valid, please try again"));
    }

    @Test
    void findByIdSuccess(){
        VideoModel video = videoService.findById(VIDEO_UUID);
        assertEquals(video,VIDEO);
    }

    @Test
    void findByIdFailedWhenIdWasNotFound(){
        UUID uuid = UUID.fromString("f7af7de0-2c84-4111-958c-16221e9698e4");
        Exception exception = assertThrows(VideoNotFoundException.class, () ->{
            videoService.findById(uuid);
        });
        assertEquals("The video is not found, try again", exception.getMessage());
    }

    @Test
    void findByIdFailedWhenIdIsNull(){
        Exception exception = assertThrows(VideoNotFoundException.class, () ->{
            videoService.findById(null);
        });
        assertEquals("The videos is null, try again", exception.getMessage());
    }

    @Test
    void findAllVideosSuccess(){
        List<VideoModel> response = videoService.findAllVideos();
        assertEquals(3,response.size());
        assertEquals("title 2", response.get(1).getTitle());
    }

    @Test
    void findAllVideosWithPagesSuccess(){
        var response = videoService.findAllVideosWithPages(PAGEABLE);
        assertEquals(3, response.size());
    }

    @Test
    void findAllVideosByUsernameSuccess(){
        List<VideoModel> videos = videoService.findAllVideosByUsername(USERNAME);
        assertEquals(3,videos.size());
        assertEquals("title 2", videos.get(1).getTitle());
    }


    @Test
    void findAllVideosByUsernameFailedWhenUsernameIsNull(){
        Exception exception = assertThrows(InvalidFieldsException.class, () ->{
            videoService.findAllVideosByUsername(null);
        });
        assertEquals("Error, your fields is null, please try again", exception.getMessage());
    }

    @Test
    void findAllVideosByUsernameFailedWhenUsernameIsBlankOrNull(){
        Exception exception = assertThrows(InvalidFieldsException.class, () ->{
            videoService.findAllVideosByUsername("");
        });
        assertEquals("Error, your fields is blank or empty, please try again", exception.getMessage());
    }

    @Test
    void findAllVideosByUsernameFailedWhenUsernameIsNotRegistered(){
        Exception exception = assertThrows(UserNotFoundException.class, () ->{
            videoService.findAllVideosByUsername("Adam");
        });
        assertEquals("Username: Adam, not found!", exception.getMessage());
    }

    @Test
    void returnUserFromUsernameSuccess(){
        UserModel user = videoService.returnUserFromUsername(USERNAME);
        assertEquals(USER,user);
    }

    @Test
    void returnUserFromUsernameFailedWhenUsernameNotRegistered(){
        Exception exception = assertThrows(UserNotFoundException.class, ()->{
            videoService.returnUserFromUsername("Jose");
        });
        assertEquals("The username Jose not found, try again",exception.getMessage());
    }

    @Test
    void returnUserFromUsernameFailedWhenUsernameIsNull(){
        Exception exception = assertThrows(InvalidFieldsException.class, ()->{
            videoService.returnUserFromUsername(null);
        });
        assertEquals("Error, your fields is null, please try again",exception.getMessage());
    }

    @Test
    void returnUserFromUsernameFailedWhenUsernameIsBlankOrEmpty(){
        Exception exception = assertThrows(InvalidFieldsException.class, ()->{
            videoService.returnUserFromUsername("");
        });
        assertEquals("Error, your fields is blank or empty, please try again",exception.getMessage());
    }


    @Test
    void saveVideoSuccess(){
        videoService.saveVideo(VIDEO);
    }

    @Test
    void saveVideoFailedWhenVideosIsNull(){
        Exception exception = assertThrows(VideoNotFoundException.class, ()->{
            videoService.saveVideo(null);
        });
        assertEquals("The video is null, try again",exception.getMessage());
    }

    @Test
    void deleteVideoWithCommentsSuccess(){
        VideoService videoServiceMock = mock(VideoService.class);
        videoServiceMock.deleteVideoWithComments(VIDEO_UUID);
        verify(videoServiceMock, times(1)).deleteVideoWithComments(VIDEO_UUID);


    }

    @Test
    void deleteVideoWithCommentsFailedWhenUUIDIsNotFound(){
        VideoService videoServiceMock = mock(VideoService.class);
        doThrow(new VideoNotFoundException("The UUID of video is not valid, please try again")).when(videoServiceMock).deleteVideoWithComments(VIDEO_UUID_INVALID);
        Exception exception = assertThrows(VideoNotFoundException.class, () ->{
            videoService.deleteVideoWithComments(VIDEO_UUID_INVALID);
        });
        assertEquals("The UUID of video is not valid, please try again", exception.getMessage());
    }

    @Test
    void deleteVideoWithoutCommentsSuccess(){
        VideoService videoServiceMock = mock(VideoService.class);
        videoServiceMock.deleteVideoWithoutComments(VIDEO_UUID);
        verify(videoServiceMock, times(1)).deleteVideoWithoutComments(VIDEO_UUID);
    }

    @Test
    void deleteVideoWithoutCommentsFailedWhenUUIDIsNotFound(){
        VideoService videoServiceMock = mock(VideoService.class);
        doThrow(new VideoNotFoundException("The UUID of video is not valid, please try again")).when(videoServiceMock).deleteVideoWithoutComments(VIDEO_UUID_INVALID);
        Exception exception = assertThrows(VideoNotFoundException.class, () ->{
            videoService.deleteVideoWithoutComments(VIDEO_UUID_INVALID);
        });
        assertEquals("The UUID of video is not valid, please try again", exception.getMessage());
    }

    @Test
    void findUsernameByVideoIdSuccess(){
        var response = videoService.findUsernameByVideoId(VIDEO_UUID);
        assertEquals(String.valueOf(VIDEO_UUID), response);
    }


    @Test
    void findUsernameByVideoIdFailedWhenUUIDIsNotFound(){
        Exception exception = assertThrows(VideoNotFoundException.class, () ->{
            videoService.deleteVideoWithoutComments(VIDEO_UUID_INVALID);
        });
        assertEquals("The UUID of video is not valid, please try again", exception.getMessage());
    }






}