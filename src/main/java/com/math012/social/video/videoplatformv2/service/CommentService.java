package com.math012.social.video.videoplatformv2.service;

import com.math012.social.video.videoplatformv2.exception.CommentException;
import com.math012.social.video.videoplatformv2.exception.UserNotFoundException;
import com.math012.social.video.videoplatformv2.exception.VideoNotFoundException;
import com.math012.social.video.videoplatformv2.model.CommentModel;
import com.math012.social.video.videoplatformv2.repository.CommentRepository;
import com.math012.social.video.videoplatformv2.repository.UserRepository;
import com.math012.social.video.videoplatformv2.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CommentService {

    @Autowired
    public CommentRepository commentRepository;

    @Autowired
    public UserRepository userRepository;

    @Autowired
    public VideoRepository videoRepository;


    public CommentModel postComment(String username, UUID idVideo, String textContent){
        if (username == null) throw new UserNotFoundException("The username is null, try again");
        if (idVideo == null) throw new VideoNotFoundException("The video id is null, try again");
        if (textContent == null) throw new CommentException("The text content is null, try again");

        if (userRepository.findByUsername(username).isEmpty()) throw new UserNotFoundException("The username " + username+" not found, try again");
        if (videoRepository.findById(idVideo).isEmpty()) throw new VideoNotFoundException("The id " + idVideo+" not found, try again");

        var user = userRepository.findByUsername(username).get();
        var video = videoRepository.findById(idVideo).get();
        var comment = new CommentModel(textContent,user,video);
        commentRepository.save(comment);
        return comment;
    }

    public List<CommentModel> findAllCommentsByVideo(UUID videoId){
        if (videoId == null) throw new VideoNotFoundException("The video id is null, try again");
        if (videoRepository.findById(videoId).isEmpty()) throw new VideoNotFoundException("The video id " + videoId + " not found");
        var video = videoRepository.findById(videoId).get();
        return commentRepository.findAllCommentByVideo(video);
    }

    public void deleteCommentById(UUID id){
        if (commentRepository.findById(id).isEmpty()) throw new CommentException("The UUID of comment is not valid, please try again");
        commentRepository.deleteById(id);
    }

    public void deleteCommentByVideoId(UUID id){
        if (videoRepository.findById(id).isEmpty()) throw new VideoNotFoundException("The UUID of video is not valid, please try again");
        commentRepository.deleteCommentByVideoId(id);
    }
}
