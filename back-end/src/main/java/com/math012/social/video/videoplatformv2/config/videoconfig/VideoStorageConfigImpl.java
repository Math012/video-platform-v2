package com.math012.social.video.videoplatformv2.config.videoconfig;

import com.math012.social.video.videoplatformv2.config.interfaces.StorageConfig;
import com.math012.social.video.videoplatformv2.exception.LoadVideoException;
import com.math012.social.video.videoplatformv2.exception.UserNotFoundException;
import com.math012.social.video.videoplatformv2.exception.VideoStorageException;
import com.math012.social.video.videoplatformv2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Configuration
@ConfigurationProperties(prefix = "file")
public class VideoStorageConfigImpl implements StorageConfig {

    @Value("${file.upload-dir:dir}")
    private  String pathDir = "dir";

    private UserRepository userRepository;

    public VideoStorageConfigImpl(UserRepository userRepository){
        this.userRepository = userRepository;
    }


    @Override
    public String saveFile(MultipartFile file, String username) {
        if (userRepository.findByUsername(username).isEmpty()) throw new UserNotFoundException("This username: " + username + ", not found");
        if (file == null) throw new VideoStorageException("The video was not saved, please try again");

        String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        Path path = Path.of(this.pathDir);
        createFolder(path,"\\",username);
        Path pathFolder = Path.of(path+"\\"+username);
        if (!filename.contains(".mp4")) throw new VideoStorageException("Please, send mp4 format only");
        try {
            Path location = pathFolder.resolve(filename);
            Files.copy(file.getInputStream(),location, StandardCopyOption.REPLACE_EXISTING);
            return filename;
        }catch (Exception e){
            return e.getMessage();
        }
    }

    @Override
    public Resource loadFile(String path, String username) {
        if (username == null) throw new UserNotFoundException("username invalid, try again");
        try {
            Path pathFile = Path.of(pathDir);
            Path locationPath = Path.of(pathFile+"\\"+username);
            Path resolvePath = locationPath.resolve(path).normalize();
            Resource resource = new UrlResource(resolvePath.toUri());
            return resource;
        }catch (Exception e){
            throw new LoadVideoException("Download URL was not found");
        }
    }

    private void createFolder(Path path, String limiter, String username) {
        new File(path + limiter + username).mkdir();
    }
}
