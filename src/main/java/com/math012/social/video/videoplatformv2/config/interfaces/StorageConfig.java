package com.math012.social.video.videoplatformv2.config.interfaces;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface StorageConfig {

     String saveFile(MultipartFile file, String username);


     Resource loadFile(String path, String username);
}
