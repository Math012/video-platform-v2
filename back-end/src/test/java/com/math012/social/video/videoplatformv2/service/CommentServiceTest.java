package com.math012.social.video.videoplatformv2.service;

import com.math012.social.video.videoplatformv2.exception.CommentException;
import com.math012.social.video.videoplatformv2.exception.UserNotFoundException;
import com.math012.social.video.videoplatformv2.exception.VideoNotFoundException;
import com.math012.social.video.videoplatformv2.model.CommentModel;
import com.math012.social.video.videoplatformv2.model.UserModel;
import com.math012.social.video.videoplatformv2.model.VideoModel;
import com.math012.social.video.videoplatformv2.repository.CommentRepository;
import com.math012.social.video.videoplatformv2.repository.UserRepository;
import com.math012.social.video.videoplatformv2.repository.VideoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest
public class CommentServiceTest {

    @Autowired
    CommentService commentService;

    @MockBean
    UserRepository userRepository;

    @MockBean
    VideoRepository videoRepository;

    @MockBean
    CommentRepository commentRepository;

    private static final UserModel USER = new UserModel("Bob", "Lau", "BobLau","12345");
    private static final UUID VIDEO_UUID = UUID.randomUUID();
    private static final UUID VIDEO_UUID_INVALID = UUID.randomUUID();
    private static final VideoModel VIDEO = new VideoModel("title","description","video url",USER, Instant.now());
    private static final String USERNAME = "BobLau";
    private static final String TEXT_COMMENT = "Content commentary";
    private static final CommentModel COMMENT_MODEL = new CommentModel(USERNAME,USER,VIDEO);


    @BeforeEach
    void setup(){
        List<CommentModel> comments = List.of(
                new CommentModel("content 1",USER,VIDEO)
                ,new CommentModel("content 2",USER,VIDEO)
                ,new CommentModel("content 3",USER,VIDEO));


        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(USER));
        when(videoRepository.findById(VIDEO_UUID)).thenReturn(Optional.of(VIDEO));
        when(commentRepository.save(COMMENT_MODEL)).thenReturn(COMMENT_MODEL);
        when(commentRepository.findAllCommentByVideo(VIDEO)).thenReturn(comments);
        doNothing().when(commentRepository).deleteById(VIDEO_UUID);
        //doNothing().when(commentRepository).deleteCommentByVideoId(VIDEO_UUID);


    }

    @Test
    void postCommentSuccess(){
        VIDEO.setId(VIDEO_UUID);
        CommentModel commentModel = commentService.postComment(USERNAME,VIDEO_UUID,TEXT_COMMENT);
        assertEquals("Content commentary", commentModel.getText());
        assertEquals("BobLau", commentModel.getUser().getUsername());
        assertEquals(VIDEO_UUID,commentModel.getVideo().getId());
    }

    @Test
    void postCommentFailedWhenUsernameIsNull(){
        Exception exception = assertThrows(UserNotFoundException.class, () ->{
            commentService.postComment(null,VIDEO_UUID,TEXT_COMMENT);
        });
        assertEquals("The username is null, try again",exception.getMessage());
    }

    @Test
    void postCommentFailedWhenVideoIdIsNull(){
        Exception exception = assertThrows(VideoNotFoundException.class, () ->{
            commentService.postComment(USERNAME,null,TEXT_COMMENT);
        });
        assertEquals("The video id is null, try again",exception.getMessage());
    }

    @Test
    void postCommentFailedWhenVTextContentIsNull(){
        Exception exception = assertThrows(CommentException.class, () ->{
            commentService.postComment(USERNAME,VIDEO_UUID,null);
        });
        assertEquals("The text content is null, try again",exception.getMessage());
    }

    @Test
    void postCommentFailedWhenUsernameNotRegistered(){
        Exception exception = assertThrows(UserNotFoundException.class, () ->{
            commentService.postComment("Adam",VIDEO_UUID,TEXT_COMMENT);
        });
        assertEquals("The username Adam not found, try again",exception.getMessage());
    }

    @Test
    void postCommentFailedWhenVideoIdNotFound(){
        UUID videoId = UUID.randomUUID();
        Exception exception = assertThrows(VideoNotFoundException.class, () ->{
            commentService.postComment(USERNAME,videoId,TEXT_COMMENT);
        });
        assertEquals("The id " + videoId+" not found, try again",exception.getMessage());
    }

    @Test
    void findAllCommentsByVideoSuccess(){
        List<CommentModel> comments = commentService.findAllCommentsByVideo(VIDEO_UUID);
        assertEquals(3, comments.size());
        assertEquals("content 2", comments.get(1).getText());
    }

    @Test
    void findAllCommentsByVideoFailedWhenVideoIdIsNull(){
        Exception exception = assertThrows(VideoNotFoundException.class, () ->{
            commentService.findAllCommentsByVideo(null);
        });
        assertEquals("The video id is null, try again",exception.getMessage());
    }

    @Test
    void findAllCommentsByVideoFailedWhenVideoIdNotFound(){
        UUID videoId = UUID.randomUUID();
        Exception exception = assertThrows(VideoNotFoundException.class, () ->{
            commentService.findAllCommentsByVideo(videoId);
        });
        assertEquals("The video id " + videoId + " not found",exception.getMessage());
    }

    @Test
    void deleteCommentByIdSuccess(){
        CommentService commentServiceMock = mock(CommentService.class);
        commentServiceMock.deleteCommentById(VIDEO_UUID);
       verify(commentServiceMock, times(1)).deleteCommentById(VIDEO_UUID);
    }

    @Test
    void deleteCommentByIdFailedWhenUUIDIsNotFound(){
        CommentService commentServiceMock = mock(CommentService.class);
        doThrow(new CommentException("The UUID of comment is not valid, please try again")).when(commentServiceMock).deleteCommentById(VIDEO_UUID_INVALID);
        Exception exception = assertThrows(CommentException.class, ()->{
            commentServiceMock.deleteCommentById(VIDEO_UUID_INVALID);
        });
        assertEquals("The UUID of comment is not valid, please try again", exception.getMessage());
        verify(commentServiceMock, times(1)).deleteCommentById(VIDEO_UUID_INVALID);
    }

    @Test
    void deleteCommentByVideoIdSuccess(){
        CommentService commentServiceMock = mock(CommentService.class);
        commentServiceMock.deleteCommentByVideoId(VIDEO_UUID);
        verify(commentServiceMock,times(1)).deleteCommentByVideoId(VIDEO_UUID);
    }

    @Test
    void deleteCommentByVideoIdFailedWhenUUIDIsNotFound(){
        CommentService commentServiceMock = mock(CommentService.class);
        doThrow(new VideoNotFoundException("The UUID of video is not valid, please try again")).when(commentServiceMock).deleteCommentByVideoId(VIDEO_UUID_INVALID);
        Exception exception = assertThrows(VideoNotFoundException.class, () ->{
            commentService.deleteCommentByVideoId(VIDEO_UUID_INVALID);
        });
        assertEquals("The UUID of video is not valid, please try again", exception.getMessage());
    }


}
