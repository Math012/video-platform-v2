package com.math012.social.video.videoplatformv2.service;

import com.math012.social.video.videoplatformv2.config.videoconfig.VideoStorageConfigImpl;
import com.math012.social.video.videoplatformv2.exception.UserNotFoundException;
import com.math012.social.video.videoplatformv2.exception.VideoNotFoundException;
import com.math012.social.video.videoplatformv2.model.UserModel;
import com.math012.social.video.videoplatformv2.model.VideoModel;
import com.math012.social.video.videoplatformv2.repository.UserRepository;
import com.math012.social.video.videoplatformv2.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class VideoService {

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private UserRepository userRepository;

    public List<VideoModel> findAllVideos(){
        return videoRepository.findAll();
    }


    public List<VideoModel> findAllVideosByUsername(String username){
        var user = userRepository.findByUsername(username);
        if (user.isEmpty()) throw new UserNotFoundException("Username: " + username + ", not found!");
        return videoRepository.findAllVideosByUsername(user.get());
    }

    public VideoModel uploadVideo(VideoModel videoModel, String username){
        var user = userRepository.findByUsername(username);
        if (videoModel == null) throw new VideoNotFoundException("No videos found, try again!");
        if (user.isEmpty()) throw new UserNotFoundException("User not found, please try again");
        videoModel.setDate(Instant.now());
        videoModel.setUser(user.get());
        return videoModel;
    }


}