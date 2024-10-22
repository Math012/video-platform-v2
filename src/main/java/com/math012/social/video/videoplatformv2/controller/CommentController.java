package com.math012.social.video.videoplatformv2.controller;

import com.math012.social.video.videoplatformv2.DTO.CommentDTO;
import com.math012.social.video.videoplatformv2.model.CommentModel;
import com.math012.social.video.videoplatformv2.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin("*")
public class CommentController {

    @Autowired
    private CommentService commentService;

   @PostMapping("api/v2/video/comment/{username}/{videoId}")
    public CommentModel postComment(@PathVariable(value = "username")String username, @PathVariable(value = "videoId")UUID id,@RequestBody CommentDTO commentDTO){
       return commentService.postComment(username,id,commentDTO.textContent());
   }

   @GetMapping("comment/video/{idVideo}")
    public List<CommentModel> findAllCommentsByVideo(@PathVariable(value = "idVideo")UUID idVideo){
        return commentService.findAllCommentsByVideo(idVideo);
   }

   @DeleteMapping(path = "api/v2/video/comment/{videoId}")
    public ResponseEntity<String> deleteComment(@PathVariable(value = "videoId")UUID videoId){
       commentService.deleteCommentById(videoId);
       return ResponseEntity.ok().body("Comment successfully deleted");
   }
}
