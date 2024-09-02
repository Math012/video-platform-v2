package com.math012.social.video.videoplatformv2.service;

import com.math012.social.video.videoplatformv2.model.CommentModel;
import com.math012.social.video.videoplatformv2.model.VideoModel;
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
        var user = userRepository.findByUsername(username).get();
        var video = videoRepository.findById(idVideo).get();
        var comment = new CommentModel(textContent,user,video);
        commentRepository.save(comment);
        return comment;
    }

    public List<CommentModel> findAllCommentsByVideo(UUID videoId){
        var video = videoRepository.findById(videoId).get();
        return commentRepository.findAllCommentByVideo(video);
    }
}
