package com.math012.social.video.videoplatformv2.repository;

import com.math012.social.video.videoplatformv2.model.UserModel;
import com.math012.social.video.videoplatformv2.model.VideoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserModel, UUID> {
    Optional<UserModel> findByUsername(String username);

    @Query("SELECT videos FROM UserModel u WHERE u.username = :username")
    List<VideoModel> findAllVideosByUsername(String username);

    @Query("SELECT user.username FROM UserModel user WHERE user.id=:userId")
    String findUsernameById(@Param("userId")UUID userId);

}


