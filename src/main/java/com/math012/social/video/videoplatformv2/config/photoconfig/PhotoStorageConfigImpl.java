package com.math012.social.video.videoplatformv2.config.photoconfig;

import com.math012.social.video.videoplatformv2.config.interfaces.StorageConfig;
import com.math012.social.video.videoplatformv2.exception.PhotoProfileException;
import com.math012.social.video.videoplatformv2.exception.UserNotFoundException;
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
public class PhotoStorageConfigImpl implements StorageConfig {

    @Value("${file.upload-dir:dir}")
    private  String pathDir = "dir";


    private UserRepository userRepository;

    public PhotoStorageConfigImpl(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public String saveFile(MultipartFile file, String username) {
        if (userRepository.findByUsername(username).isEmpty()) throw new UserNotFoundException("This username: " + username + ", not found");
        if (file == null) throw new PhotoProfileException("The profile photo is null, please try again");


        String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        Path path = Path.of(this.pathDir);

        createFolderForProfilePhoto(path,"\\",username,"photoProfile");

        Path pathFolder = Path.of(path+"\\"+username+"\\"+"photoProfile");

        if (filename.contains(".mp4")) throw new PhotoProfileException("Video upload is not supported for profile photos");

        try {
            Path location = pathFolder.resolve(filename);
            Files.copy(file.getInputStream(),location, StandardCopyOption.REPLACE_EXISTING);
            return filename;
        }catch (Exception e){
            throw new PhotoProfileException("The profile photo was not saved, please try again");
        }
    }

    @Override
    public Resource loadFile(String path, String username) {
        if (username == null) throw new UserNotFoundException("username invalid, try again");
        try {
            Path pathFile = Path.of(pathDir);
            Path locationPath = Path.of(pathFile+"\\"+username+"\\"+"photoProfile");
            Path resolvePath = locationPath.resolve(path).normalize();
            Resource resource = new UrlResource(resolvePath.toUri());
            return resource;
        }catch (Exception e){
            throw new PhotoProfileException("Download profile photo URL was not found");
        }
    }

    private void createFolderForProfilePhoto(Path path, String limiter, String username, String photoProfile) {
        new File(path + limiter + username).mkdir();
        new File(path + limiter + username + limiter + photoProfile).mkdir();
    }
}
