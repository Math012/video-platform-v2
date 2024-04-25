package com.math012.social.video.videoplatformv2.config.videoconfig;

import com.math012.social.video.videoplatformv2.config.videoconfig.interfaces.VideoStorageConfig;
import com.math012.social.video.videoplatformv2.exception.UserNotFoundException;
import com.math012.social.video.videoplatformv2.exception.VideoStorageException;
import com.math012.social.video.videoplatformv2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/*
        A ideia inicial é criar um diretorio no disco local C para salvar todos os videos.
        O caminho foi definido através do application.properties.
 */


@Configuration
@ConfigurationProperties(prefix = "file")
public class VideoStorageConfigImpl implements VideoStorageConfig {

    @Value("${file.upload-dir:dir}")
    private  String pathDir = "dir";

    private UserRepository userRepository;

    public VideoStorageConfigImpl(UserRepository userRepository){
        this.userRepository = userRepository;
    }



    @Override
    public String saveFile(MultipartFile file, String username) {
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        Path path = Path.of(this.pathDir);

        if (userRepository.findByUsername(username).isEmpty()) throw new UserNotFoundException("This username: " + username + ", not found");
        createFolder(path,"\\",username);
        Path pathFolder = Path.of(path+"\\"+username);
        if (!filename.contains(".mp4")) throw new VideoStorageException("Upload only videos");
        try {
            Path location = pathFolder.resolve(filename);
            Files.copy(file.getInputStream(),location, StandardCopyOption.REPLACE_EXISTING);
            return filename;
        }catch (Exception e){
            throw new VideoStorageException("The video was not saved, please try again");
        }
    }

    private void createFolder(Path path, String limiter, String username) {
        new File(path + limiter + username).mkdir();
    }
}
