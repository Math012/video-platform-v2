package com.math012.social.video.videoplatformv2.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.gson.Gson;
import com.math012.social.video.videoplatformv2.DTO.CommentDTO;
import com.math012.social.video.videoplatformv2.config.security.TokenProvider;
import com.math012.social.video.videoplatformv2.exception.CommentException;
import com.math012.social.video.videoplatformv2.exception.UserNotFoundException;
import com.math012.social.video.videoplatformv2.exception.VideoNotFoundException;
import com.math012.social.video.videoplatformv2.model.CommentModel;
import com.math012.social.video.videoplatformv2.model.UserModel;
import com.math012.social.video.videoplatformv2.model.VideoModel;
import com.math012.social.video.videoplatformv2.repository.UserRepository;
import com.math012.social.video.videoplatformv2.service.CommentService;
import com.math012.social.video.videoplatformv2.service.UserService;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class CommentControllerTest {

    // COMMENT
    private static final UUID UUID_USER = java.util.UUID.randomUUID();
    private static final UUID UUID_VIDEO = java.util.UUID.randomUUID();
    private static final UUID UUID_VIDEO_INVALID = java.util.UUID.randomUUID();
    private static final UUID UUID_USER_NOT_FOUND = java.util.UUID.randomUUID();
    private static final CommentDTO COMMENT_DTO = new CommentDTO("content of commentary");

    // SECURITY
    private static final String USERNAME_AUTHENTICATED = "Megan345";
    private static final UserModel USER = new UserModel("Megan","Alice",USERNAME_AUTHENTICATED,"12345");
    Algorithm algorithm = Algorithm.HMAC256("12345");
    Date date = new Date();
    long validationTime = 18000000;
    String TOKEN_JWT = JWT.create()
            .withIssuer("auth")
            .withSubject(USERNAME_AUTHENTICATED)
            .withExpiresAt(new Date(date.getTime() + validationTime))
            .sign(algorithm);

    @Autowired
    CommentController commentController;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserRepository userRepository;

    @MockBean
    UserService userService;

    @MockBean
    CommentService commentService;

    @MockBean
    TokenProvider tokenProvider;

    @MockBean
    private FilterChain filterChain;

    @BeforeEach
    void setup(){
        // CONFIGURATION OF JWT TOKEN
        when(userRepository.findByUsername(USERNAME_AUTHENTICATED)).thenReturn(Optional.of(USER));
        when(userService.returnUserFromUsername(USERNAME_AUTHENTICATED)).thenReturn(USER);

        // POST COMMENT TEST
        VideoModel video = new VideoModel("title video", "description video","url video",USER, Instant.now());
        CommentModel commentModel = new CommentModel("commentary content", USER,video);
        when(commentService.postComment(USERNAME_AUTHENTICATED,UUID_USER,COMMENT_DTO.textContent())).thenReturn(commentModel);
        when(commentService.postComment("Paul",UUID_USER,COMMENT_DTO.textContent())).thenThrow(new UserNotFoundException("The username Paul not found, try again"));
        when(commentService.postComment(USERNAME_AUTHENTICATED,UUID_USER_NOT_FOUND,COMMENT_DTO.textContent())).thenThrow(new VideoNotFoundException("The id " + UUID_USER_NOT_FOUND +" not found, try again"));
        // FIND ALL COMMENTS BY VIDEO
        List<CommentModel> comments = List.of(
                new CommentModel("commentary content 1", USER,video),
                new CommentModel("commentary content 2", USER,video),
                new CommentModel("commentary content 3", USER,video)
        );

        when(commentService.findAllCommentsByVideo(UUID_USER)).thenReturn(comments);
        when(commentService.findAllCommentsByVideo(UUID_USER_NOT_FOUND)).thenThrow(new VideoNotFoundException("The id " + UUID_USER_NOT_FOUND +" not found, try again"));
        // DELETE VIDEO BY ID
        doNothing().when(commentService).deleteCommentById(UUID_VIDEO);
        doThrow(new CommentException("The UUID of comment is not valid, please try again")).when(commentService).deleteCommentById(UUID_VIDEO_INVALID);



    }

    @Test
    void postCommentSuccess() throws Exception {
        var json = new Gson().toJson(COMMENT_DTO);
        mockMvc.perform(post("/api/v2/video/comment/{username}/{videoId}",USERNAME_AUTHENTICATED, UUID_USER)
                .content(json)
                .header("Authorization", TOKEN_JWT)
                .contentType(MediaType.APPLICATION_JSON)

        ).andExpect(status().isOk())
        ;
    }

    @Test
    void postCommentFailedUserNotAuthenticated() throws Exception {
        var json = new Gson().toJson(COMMENT_DTO);
        mockMvc.perform(post("/api/v2/video/comment/{username}/{videoId}",USERNAME_AUTHENTICATED, UUID_USER)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)

        )
                .andExpect(status().isForbidden())
        ;
    }

    @Test
    void postCommentFailedWhenUsernameIsNotRegistered() throws Exception {
        var json = new Gson().toJson(COMMENT_DTO);
        mockMvc.perform(post("/api/v2/video/comment/{username}/{videoId}","Paul", UUID_USER)
                .content(json)
                .header("Authorization", TOKEN_JWT)
                .contentType(MediaType.APPLICATION_JSON)

        )
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserNotFoundException))
                .andExpect(jsonPath("$.msg").value("The username Paul not found, try again"))
                .andExpect(jsonPath("$.timestamp").exists())
        ;
    }
    @Test
    void postCommentFailedWhenUUIDIsNotFound() throws Exception {
        var json = new Gson().toJson(COMMENT_DTO);
        mockMvc.perform(post("/api/v2/video/comment/{username}/{videoId}",USERNAME_AUTHENTICATED, UUID_USER_NOT_FOUND)
                        .content(json)
                        .header("Authorization", TOKEN_JWT)
                        .contentType(MediaType.APPLICATION_JSON)

                )
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof VideoNotFoundException))
                .andExpect(jsonPath("$.msg").value("The id " + UUID_USER_NOT_FOUND+" not found, try again"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void findAllCommentsByVideoSuccess() throws Exception {
        mockMvc.perform(get("/comment/video/{idVideo}",UUID_USER)
                .contentType(MediaType.APPLICATION_JSON)

        )
                .andExpect(status().isOk())
        ;
    }

    @Test
    void findAllCommentsByVideoFailedWhenUUIDIsNotFound() throws Exception {
        mockMvc.perform(get("/comment/video/{idVideo}",UUID_USER_NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof VideoNotFoundException))
                .andExpect(jsonPath("$.msg").value("The id " + UUID_USER_NOT_FOUND+" not found, try again"))
                .andExpect(jsonPath("$.timestamp").exists())
        ;
    }

    @Test
    void deleteCommentByIdSuccess() throws Exception {
        mockMvc.perform(delete("/api/v2/video/comment/{videoId}",UUID_VIDEO)
                .header("Authorization", TOKEN_JWT)
                .contentType(MediaType.APPLICATION_JSON)

        )
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(result.getResponse().getContentAsString(),"Comment successfully deleted"))
        ;
    }

    @Test
    void deleteCommentByIdFailedWhenUUIDIsNotFound() throws Exception {
        mockMvc.perform(delete("/api/v2/video/comment/{videoId}",UUID_VIDEO_INVALID)
                        .header("Authorization", TOKEN_JWT)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof CommentException))
                .andExpect(jsonPath("$.msg").value("The UUID of comment is not valid, please try again"))
                .andExpect(jsonPath("$.timestamp").exists());
        verify(commentService, times(1)).deleteCommentById(UUID_VIDEO_INVALID);
    }


}
