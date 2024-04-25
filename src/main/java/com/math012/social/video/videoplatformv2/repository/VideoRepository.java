package com.math012.social.video.videoplatformv2.repository;

import com.math012.social.video.videoplatformv2.model.UserModel;
import com.math012.social.video.videoplatformv2.model.VideoModel;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VideoRepository extends JpaRepository<VideoModel, UUID> {

    @Transactional
    @Query("SELECT video FROM VideoModel video WHERE video.user=:user")
    List<VideoModel> findAllVideosByUsername(@Param("user")UserModel userModel);
}
