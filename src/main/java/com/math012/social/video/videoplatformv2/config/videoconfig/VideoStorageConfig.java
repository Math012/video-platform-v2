package com.math012.social.video.videoplatformv2.config.videoconfig;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/*
        A ideia inicial é criar um diretorio no disco local C para salvar todos os videos.
        O caminho foi especificado através do application.properties.
 */


@Configuration
@ConfigurationProperties(prefix = "file")
public class VideoStorageConfig {



}
