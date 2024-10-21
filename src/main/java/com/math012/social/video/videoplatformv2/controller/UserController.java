package com.math012.social.video.videoplatformv2.controller;

import com.math012.social.video.videoplatformv2.DTO.DescriptionDTO;
import com.math012.social.video.videoplatformv2.DTO.RegisterDTO;
import com.math012.social.video.videoplatformv2.DTO.UserDTO;
import com.math012.social.video.videoplatformv2.config.photoconfig.PhotoStorageConfigImpl;
import com.math012.social.video.videoplatformv2.exception.VideoStorageException;
import com.math012.social.video.videoplatformv2.model.UserModel;
import com.math012.social.video.videoplatformv2.model.VideoModel;
import com.math012.social.video.videoplatformv2.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@CrossOrigin("*")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private PhotoStorageConfigImpl photoStorage;

    @PostMapping("/v2/auth/login")
    public ResponseEntity<String> login(@Valid @RequestBody UserDTO userDTO){
        return userService.login(userDTO);
    }

    @PostMapping("/v2/auth/register")
    public ResponseEntity<String> register(@RequestBody RegisterDTO registerDTO){
        return userService.register(registerDTO);
    }

    @PostMapping("api/v2/post/photo/profile/{username}")
    public UserModel uploadPhotoProfile(MultipartHttpServletRequest request, @PathVariable(value = "username")String username){
        MultipartFile file = request.getFile("file");
        var user = userService.returnUserFromUsername(username);
        var photoPath = photoStorage.saveFile(file,username);
        String urlPhoto = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("api/v2/post/photo/download/")
                .path(username)
                .path("/")
                .path(photoPath)
                .toUriString();
        user.setProfilePhoto(urlPhoto);
        userService.saveUser(user);
        return user;
    }

    @GetMapping("api/v2/post/photo/download/{username}/{path:.+}")
    public ResponseEntity<Resource> download(@PathVariable(value = "username")String username, @PathVariable(value = "path") String path, HttpServletRequest request){
        Resource resource = photoStorage.loadFile(path, username);
        String content = null;

        try {
            content = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        }catch (Exception e){
            throw new VideoStorageException(e.getMessage());
        }
        if (content == null){
            content = "application/octet-stream";
        }
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(content)).header(HttpHeaders.CONTENT_DISPOSITION,"attachment; path=\""+resource.getFilename()+"\"").body(resource);
    }

    @GetMapping("/all/videos/{username}")
    public ResponseEntity<List<VideoModel>> findAllVideosByUsername(@PathVariable(value = "username")String username){
        return userService.findAllVideosByUsername(username);
    }

    @PostMapping("/change/description/user/{username}")
    public ResponseEntity<String> changeTheUserDescription(@PathVariable(value = "username")String username, @RequestBody DescriptionDTO descriptionDTO){
        return userService.changeTheUserDescription(descriptionDTO.description(), username);
    }

    @GetMapping("/all/channels")
    public ResponseEntity<List<UserModel>> findAllUsers(){
        return userService.findAllUsers();
    }

    @GetMapping("/user/info/{username}")
    public ResponseEntity<UserModel> findUserByUsername(@PathVariable(value = "username")String username){
        return userService.findUserByUsername(username);
    }


}
