package com.math012.social.video.videoplatformv2.repository;

import com.math012.social.video.videoplatformv2.model.VideoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VideoRepository extends JpaRepository<VideoModel, UUID> {
}
