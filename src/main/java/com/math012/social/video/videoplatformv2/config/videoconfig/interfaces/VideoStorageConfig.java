package com.math012.social.video.videoplatformv2.config.videoconfig.interfaces;

import org.springframework.web.multipart.MultipartFile;

public interface VideoStorageConfig {

    public String saveFile(MultipartFile file, String username);
}
