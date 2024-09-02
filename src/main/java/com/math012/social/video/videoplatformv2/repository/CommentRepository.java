package com.math012.social.video.videoplatformv2.repository;

import com.math012.social.video.videoplatformv2.model.CommentModel;
import com.math012.social.video.videoplatformv2.model.VideoModel;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<CommentModel, UUID> {

    @Transactional
    @Query("SELECT comment FROM CommentModel comment WHERE comment.video=:video")
    List<CommentModel> findAllCommentByVideo(@Param("video")VideoModel videoModel);
}
