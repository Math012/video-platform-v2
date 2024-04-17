package com.math012.social.video.videoplatformv2.repository;

import com.math012.social.video.videoplatformv2.model.CommentModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CommentRepository extends JpaRepository<UUID, CommentModel> {
}
