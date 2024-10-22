package com.math012.social.video.videoplatformv2.controller;
import static org.junit.jupiter.api.Assertions.*;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.gson.Gson;
import com.math012.social.video.videoplatformv2.DTO.VideoDTO;
import com.math012.social.video.videoplatformv2.config.security.TokenProvider;
import com.math012.social.video.videoplatformv2.config.thumbnailconfig.ThumbnailStorageConfigImpl;
import com.math012.social.video.videoplatformv2.config.videoconfig.VideoStorageConfigImpl;
import com.math012.social.video.videoplatformv2.exception.*;
import com.math012.social.video.videoplatformv2.model.UserModel;
import com.math012.social.video.videoplatformv2.model.VideoModel;
import com.math012.social.video.videoplatformv2.repository.UserRepository;
import com.math012.social.video.videoplatformv2.repository.VideoRepository;
import com.math012.social.video.videoplatformv2.service.CommentService;
import com.math012.social.video.videoplatformv2.service.UserService;
import com.math012.social.video.videoplatformv2.service.VideoService;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class VideoControllerTest {

    // FIND BY ID
    private static final UUID UUID_USER = java.util.UUID.randomUUID();
    private static final UUID UUID_USER_NOT_REGISTERED = java.util.UUID.randomUUID();
    private static final UUID UUID_VIDEO = java.util.UUID.randomUUID();
    private static final UUID UUID_VIDEO_INVALID = java.util.UUID.randomUUID();
    // VIDEO UPLOAD
    private static final MockMultipartFile FILE_FORMAT_MP4 = new MockMultipartFile("file", "filename.mp4", "video/mp4", "some video content".getBytes());
    private static final MockMultipartFile FILE_THUMBNAIL = new MockMultipartFile("file", "filename.png", "image/png", "some video content".getBytes());
    private static final VideoDTO VIDEO_DTO = new VideoDTO("title","description");
    // SECURITY
    private static final String USERNAME_AUTHENTICATED = "Jon12";
    private static final String USERNAME_NOT_AUTHENTICATED = "Karl";
    private static final UserModel USER = new UserModel("Jon","Paul",USERNAME_AUTHENTICATED,"12345");
    Algorithm algorithm = Algorithm.HMAC256("12345");
    Date date = new Date();
    long validationTime = 18000000;
    String TOKEN_JWT = JWT.create()
            .withIssuer("auth")
            .withSubject(USERNAME_AUTHENTICATED)
            .withExpiresAt(new Date(date.getTime() + validationTime))
            .sign(algorithm);


    @Autowired
    VideoController videoController;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    VideoStorageConfigImpl videoStorageConfig;

    @MockBean
    ThumbnailStorageConfigImpl thumbnailStorageConfig;

    @MockBean
    VideoService videoService;

    @MockBean
    VideoRepository videoRepository;

    @MockBean
    UserRepository userRepository;

    @MockBean
    UserService userService;

    @MockBean
    TokenProvider tokenProvider;

    @MockBean
    private FilterChain filterChain;


    @BeforeEach
    void setup() throws IOException {

        VideoModel video = new VideoModel("title","description","url",USER, Instant.now());

        List<VideoModel> videos = List.of(
                new VideoModel("title 1","description 1","url 1",USER, Instant.now()),
                new VideoModel("title 2","description 2","url 2",USER, Instant.now()),
                new VideoModel("title 3","description 3","url 3",USER, Instant.now()),
                new VideoModel("title 4","description 4","url 4",USER, Instant.now())
        );

        // CONFIGURATION OF JWT TOKEN
        USER.setId(UUID_USER);
        when(userRepository.findByUsername(USERNAME_AUTHENTICATED)).thenReturn(Optional.of(USER));
        when(userService.returnUserFromUsername(USERNAME_AUTHENTICATED)).thenReturn(USER);

        // FIND BY ID
        when(videoService.findById(UUID_USER)).thenReturn(video);
        when(videoService.findById(UUID_USER_NOT_REGISTERED)).thenThrow(new VideoNotFoundException("The video is not found, try again"));

        // FIND ALL VIDEOS BY USERNAME

        when(videoService.findAllVideosByUsername(USERNAME_AUTHENTICATED)).thenReturn(videos);
        when(videoService.findAllVideosByUsername(USERNAME_NOT_AUTHENTICATED)).thenThrow(new UserNotFoundException("Username: Karl, not found!"));


        // VIDEO UPLOAD
        when(videoStorageConfig.saveFile(FILE_FORMAT_MP4,USERNAME_AUTHENTICATED)).thenReturn(FILE_FORMAT_MP4.getOriginalFilename());
        when(thumbnailStorageConfig.saveFile(FILE_THUMBNAIL,USERNAME_AUTHENTICATED)).thenReturn(FILE_THUMBNAIL.getOriginalFilename());
        when(videoStorageConfig.saveFile(FILE_THUMBNAIL,USERNAME_AUTHENTICATED)).thenThrow(new VideoStorageException("Please, send mp4 format only"));
        doNothing().when(videoService).saveVideo(video);

        // DELETE VIDEO WITH COMMENT
        doNothing().when(videoService).deleteVideoWithComments(UUID_VIDEO);
        doNothing().when(videoService).deleteVideoWithoutComments(UUID_VIDEO);
        doThrow(new VideoNotFoundException("The UUID of video is not valid, please try again")).when(videoService).deleteVideoWithComments(UUID_VIDEO_INVALID);
        doThrow(new VideoNotFoundException("The UUID of video is not valid, please try again")).when(videoService).deleteVideoWithoutComments(UUID_VIDEO_INVALID);


    }

    @Test
    void findByIdSuccess() throws Exception {
        mockMvc.perform(get("/video/detail/{id}",UUID_USER)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
        ;
    }

    @Test
    void findByIdFailedWhenUserIdIsNotRegistered() throws Exception {
        mockMvc.perform(get("/video/detail/{id}",UUID_USER_NOT_REGISTERED)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof VideoNotFoundException))
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.timestamp").exists())

        ;
    }

    @Test
    void findAllVideosByUsernameSuccess() throws Exception {
        mockMvc.perform(get("/videos/user/{username}",USERNAME_AUTHENTICATED)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
        ;
    }

    @Test
    void findAllVideosByUsernameFailedWhenUsernameIsNotRegistered() throws Exception {
        mockMvc.perform(get("/videos/user/{username}",USERNAME_NOT_AUTHENTICATED)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserNotFoundException))
                .andExpect(jsonPath("$.msg").value("Username: Karl, not found!"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andDo(print())
        ;
    }

    @Test
    void uploadVideoSuccessWithThumbnailFile() throws Exception{
        var json = new Gson().toJson(VIDEO_DTO);
        mockMvc.perform(multipart("/api/v2/post/video/{username}", USERNAME_AUTHENTICATED)
                .file(FILE_FORMAT_MP4)
                .file("thumbnail",FILE_THUMBNAIL.getBytes())
                .param("videoDTO", String.valueOf(json))
                .header("Authorization",TOKEN_JWT)
                .contentType(MediaType.MULTIPART_FORM_DATA)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.url").exists())
                .andExpect(jsonPath("$.date").exists())
                .andExpect(jsonPath("$.thumbnail").exists())
        ;
    }

    @Test
    void uploadVideoFailedWithThumbnailFileNotAuthenticated() throws Exception{
        var json = new Gson().toJson(VIDEO_DTO);
        mockMvc.perform(multipart("/api/v2/post/video/{username}", USERNAME_AUTHENTICATED)
                        .file(FILE_FORMAT_MP4)
                        .file("thumbnail",FILE_THUMBNAIL.getBytes())
                        .param("videoDTO", String.valueOf(json))
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isForbidden())
        ;
    }

    @Test
    void uploadVideoSuccessWithoutThumbnailFile() throws Exception{
        var json = new Gson().toJson(VIDEO_DTO);
        mockMvc.perform(multipart("/api/v2/post/video/{username}", USERNAME_AUTHENTICATED)
                .file(FILE_FORMAT_MP4)
                .param("videoDTO", String.valueOf(json))
                .header("Authorization",TOKEN_JWT)
                .contentType(MediaType.MULTIPART_FORM_DATA)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.url").exists())
                .andExpect(jsonPath("$.date").exists())
        ;
    }

    @Test
    void uploadVideoFailedWithoutThumbnailFileNotAuthenticated() throws Exception{
        var json = new Gson().toJson(VIDEO_DTO);
        mockMvc.perform(multipart("/api/v2/post/video/{username}", USERNAME_AUTHENTICATED)
                        .file(FILE_FORMAT_MP4)
                        .param("videoDTO", String.valueOf(json))
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isForbidden())

        ;
    }

    @Test
    void uploadVideoFailedWhenVideoFormatIsNotMp4() throws Exception{
        var json = new Gson().toJson(VIDEO_DTO);
        mockMvc.perform(multipart("/api/v2/post/video/{username}", USERNAME_AUTHENTICATED)
                .file(FILE_THUMBNAIL)
                .file("thumbnail",FILE_THUMBNAIL.getBytes())
                .param("videoDTO", String.valueOf(json))
                .header("Authorization",TOKEN_JWT)
                .contentType(MediaType.MULTIPART_FORM_DATA)
        )
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof VideoStorageException))
                .andExpect(jsonPath("$.msg").value("Please, send mp4 format only"))
                .andExpect(jsonPath("$.timestamp").exists())
        ;
    }

    @Test
    void deleteVideoWithCommentsSuccess() throws Exception {
        mockMvc.perform(delete("/api/v2/video/comment/delete/{videoId}",UUID_VIDEO)
                .header("Authorization", TOKEN_JWT)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(result.getResponse().getContentAsString(), "Your video was deleted"))
        ;
    }

    @Test
    void deleteVideoWithCommentsFailedNotAuthenticated() throws Exception {
        mockMvc.perform(delete("/api/v2/video/comment/delete/{videoId}",UUID_VIDEO)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isForbidden())

        ;
    }

    @Test
    void deleteVideoWithCommentsFailedWhenUUIDIsNotFound() throws Exception {
        mockMvc.perform(delete("/api/v2/video/comment/delete/{videoId}",UUID_VIDEO_INVALID)
                        .header("Authorization", TOKEN_JWT)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof VideoNotFoundException))
                .andExpect(jsonPath("$.msg").value("The UUID of video is not valid, please try again"))
                .andExpect(jsonPath("$.timestamp").exists());
        ;
    }

    @Test
    void deleteVideoWithoutCommentsSuccess() throws Exception {
        mockMvc.perform(delete("/api/v2/video/{videoId}",UUID_VIDEO)
                        .header("Authorization", TOKEN_JWT)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(result.getResponse().getContentAsString(), "Your video was deleted"))
        ;
    }

    @Test
    void deleteVideoWithoutCommentsFailedNotAuthenticated() throws Exception {
        mockMvc.perform(delete("/api/v2/video/{videoId}",UUID_VIDEO)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isForbidden())

        ;
    }

    @Test
    void deleteVideoWithoutCommentsFailedWhenUUIDIsNotFound() throws Exception {
        mockMvc.perform(delete("/api/v2/video/{videoId}",UUID_VIDEO_INVALID)
                        .header("Authorization", TOKEN_JWT)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof VideoNotFoundException))
                .andExpect(jsonPath("$.msg").value("The UUID of video is not valid, please try again"))
                .andExpect(jsonPath("$.timestamp").exists())
        ;
    }
}
