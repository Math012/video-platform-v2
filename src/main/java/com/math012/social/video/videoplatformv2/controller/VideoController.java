package com.math012.social.video.videoplatformv2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.math012.social.video.videoplatformv2.DTO.VideoDTO;
import com.math012.social.video.videoplatformv2.config.thumbnailconfig.ThumbnailStorageConfigImpl;
import com.math012.social.video.videoplatformv2.config.videoconfig.VideoStorageConfigImpl;
import com.math012.social.video.videoplatformv2.exception.VideoStorageException;
import com.math012.social.video.videoplatformv2.model.UserModel;
import com.math012.social.video.videoplatformv2.model.VideoModel;
import com.math012.social.video.videoplatformv2.service.VideoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin("*")
public class VideoController {

    @Autowired
    private VideoStorageConfigImpl videoStorage;

    @Autowired
    private ThumbnailStorageConfigImpl thumbnailStorage;

    @Autowired
    private VideoService videoService;

    @GetMapping("video/detail/{id}")
    public VideoModel findById(@PathVariable(value = "id")UUID id){
        return videoService.findById(id);
    }

    @GetMapping("videos/home")
    public List<VideoModel> findAllVideos(){
        return videoService.findAllVideos();
    }

    @GetMapping("videos/user/{username}")
    public List<VideoModel> findAllVideoByUsername(@PathVariable(value = "username")String username){
        return videoService.findAllVideosByUsername(username);
    }

    @GetMapping("videos/pageable")
    public HashSet<VideoModel> findAllVideosWithPage(Pageable pageable){
        return videoService.findAllVideosWithPages(pageable);
    }

    @PostMapping("api/v2/post/video/{username}")
    public VideoModel uploadVideo(MultipartHttpServletRequest request, @PathVariable(value = "username")String username) {
        try {
            var user = videoService.returnUserFromUsername(username);
            MultipartFile file = request.getFile("file");
            MultipartFile thumbnail = request.getFile("thumbnail");

            String dataVideo = request.getParameter("videoDTO");
            VideoDTO videoDTO = new ObjectMapper().readValue(dataVideo, VideoDTO.class);

            var videoPath = videoStorage.saveFile(file,username);

            String urlVideo = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("api/v2/post/video/download/")
                    .path(username)
                    .path("/")
                    .path(videoPath)
                    .toUriString();

            var video = new VideoModel(videoDTO.title(), videoDTO.description(), urlVideo,user, Instant.now());
            if (!(thumbnail == null)){
                var thumbnailPath = thumbnailStorage.saveFile(thumbnail,username);
                String urlThumbnail = ServletUriComponentsBuilder
                        .fromCurrentContextPath()
                        .path("api/v2/post/thumbnail/download/")
                        .path(username)
                        .path("/")
                        .path(thumbnailPath)
                        .toUriString();
                video.setThumbnail(urlThumbnail);
            }
            videoService.saveVideo(video);
            return video;
        }catch (Exception e){
            throw new VideoStorageException(e.getMessage());
        }
    }

    @GetMapping("api/v2/post/video/download/{username}/{path:.+}")
    public ResponseEntity<Resource> download(@PathVariable(value = "username")String username, @PathVariable(value = "path") String path, HttpServletRequest request){
        Resource resource = videoStorage.loadFile(path,username);
        String content = null;

        try {
            content = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());

        }catch (Exception e){
            throw new VideoStorageException("download failed");
        }
        if (content == null){
            content = "application/octet-stream";
        }

        return ResponseEntity.ok().contentType(MediaType.parseMediaType(content)).header(HttpHeaders.CONTENT_DISPOSITION,"attachment; path=\""+resource.getFilename()+"\"").body(resource);
    }

    @GetMapping("api/v2/post/thumbnail/download/{username}/{path:.+}")
    public ResponseEntity<Resource> downloadThumbnail(@PathVariable(value = "username")String username, @PathVariable(value = "path") String path, HttpServletRequest request){
        Resource resource = thumbnailStorage.loadFile(path, username);
        String content = null;

        try {
            content = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        }catch (Exception e){
            throw new VideoStorageException("download failed");
        }
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(content)).header(HttpHeaders.CONTENT_DISPOSITION,"attachment; path=\""+resource.getFilename()+"\"").body(resource);
    }

    @DeleteMapping(path = "/api/v2/video/comment/delete/{videoId}")
    public ResponseEntity<String> deleteVideoWithComment(@PathVariable(value = "videoId")UUID videoId){
        videoService.deleteVideoWithComments(videoId);
        return ResponseEntity.ok("Your video was deleted");
    }

    @DeleteMapping(path = "/api/v2/video/{videoId}")
    public ResponseEntity<String> deleteVideoWithoutComments(@PathVariable(value = "videoId")UUID videoId){
        videoService.deleteVideoWithoutComments(videoId);
        return ResponseEntity.ok("Your video was deleted");
    }

    @GetMapping(path = "/video/username/videos/{videoId}")
    public String deleteFindUsernameByVideoId(@PathVariable(value = "videoId")UUID videoId){
        return videoService.findUsernameByVideoId(videoId);
    }
}
