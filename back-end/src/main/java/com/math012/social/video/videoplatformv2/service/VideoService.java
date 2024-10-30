package com.math012.social.video.videoplatformv2.service;


import com.math012.social.video.videoplatformv2.exception.InvalidFieldsException;
import com.math012.social.video.videoplatformv2.exception.UserNotFoundException;
import com.math012.social.video.videoplatformv2.exception.VideoNotFoundException;
import com.math012.social.video.videoplatformv2.model.UserModel;
import com.math012.social.video.videoplatformv2.model.VideoModel;
import com.math012.social.video.videoplatformv2.repository.UserRepository;
import com.math012.social.video.videoplatformv2.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
public class VideoService {

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentService commentService;

    public VideoModel findById(UUID id){
        if (id == null) throw new VideoNotFoundException("The videos is null, try again");
        return videoRepository.findById(id).orElseThrow(
                ()-> new VideoNotFoundException("The video is not found, try again"));
    }

    public List<VideoModel> findAllVideos(){
        return videoRepository.findAll();
    }

    public HashSet<VideoModel> findAllVideosWithPages(Pageable pageable){
        HashSet<VideoModel> randomVideos = new HashSet<>();
        for(VideoModel page : videoRepository.findAll(pageable)){
            randomVideos.add(page);
        }
        return randomVideos;
    }

    public List<VideoModel> findAllVideosByUsername(String username){
        verifyFields(username);
        var user = userRepository.findByUsername(username);
        if (user.isEmpty()) throw new UserNotFoundException("Username: " + username + ", not found!");
        return videoRepository.findAllVideosByUsername(user.get());
    }

    public UserModel returnUserFromUsername(String username){
        verifyFields(username);
        if (userRepository.findByUsername(username).isPresent()){
            return userRepository.findByUsername(username).get();
        }else throw new UserNotFoundException("The username " + username+" not found, try again");
    }

    public void saveVideo(VideoModel video){
        if (video != null){
            videoRepository.save(video);
        }else throw new VideoNotFoundException("The video is null, try again");
    }

    public void deleteVideoWithComments(UUID id){
        if (videoRepository.findById(id).isEmpty()) throw new VideoNotFoundException("The UUID of video is not valid, please try again");
        commentService.deleteCommentByVideoId(id);
        videoRepository.deleteById(id);
    }

    public void deleteVideoWithoutComments(UUID id){
        if (videoRepository.findById(id).isEmpty()) throw new VideoNotFoundException("The UUID of video is not valid, please try again");
        videoRepository.deleteById(id);
    }

    public String findUsernameByVideoId(UUID id){
        if (videoRepository.findById(id).isEmpty()) throw new VideoNotFoundException("The UUID of video is not valid, please try again");
        UUID user =  videoRepository.findUsernameByVideoId(id);
        return userRepository.findUsernameById(user);
    }

    public void verifyFields(String... fields){
        for (String campos: fields){
            if (campos == null) throw new InvalidFieldsException("Error, your fields is null, please try again");
            if (campos.isBlank() || campos.isEmpty()) throw new InvalidFieldsException("Error, your fields is blank or empty, please try again");
        }
    }


}