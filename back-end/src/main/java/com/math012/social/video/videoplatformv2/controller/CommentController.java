package com.math012.social.video.videoplatformv2.controller;

import com.math012.social.video.videoplatformv2.DTO.CommentDTO;
import com.math012.social.video.videoplatformv2.model.CommentModel;
import com.math012.social.video.videoplatformv2.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin("*")
@Tag(name = "CommentController", description = "Controlador para gerenciar os comentários do usuário")
public class CommentController {

    @Autowired
    private CommentService commentService;

   @PostMapping("api/v2/video/comment/{username}/{videoId}")
   @Operation(summary = "Endpoint para enviar um comentário", description = "Envia um comentário com base no username do usuário e o id do vídeo")
   @ApiResponse(responseCode = "200", description = "Vídeo retornado com sucesso")
   @ApiResponse(responseCode = "400", description = "Comentário não enviado porque o texto do conteúdo é nulo")
   @ApiResponse(responseCode = "403", description = "Comentário não enviado porque a autenticação do usuário falhou")
   @ApiResponse(responseCode = "404", description = "Comentário não enviado porque o username não foi encontrado")
   @ApiResponse(responseCode = "404", description = "Comentário não enviado porque o id do vídeo não foi encontrado")
   @ApiResponse(responseCode = "500", description = "Erro no servidor")
   public CommentModel postComment(@PathVariable(value = "username")String username, @PathVariable(value = "videoId")UUID id,@RequestBody CommentDTO commentDTO){
       return commentService.postComment(username,id,commentDTO.textContent());
   }

   @GetMapping("comment/video/{idVideo}")
   @Operation(summary = "Endpoint para recuperar uma lista de comentários", description = "Recupera uma lista de comentário com base no id do vídeo")
   @ApiResponse(responseCode = "200", description = "Comentários retornados com sucesso")
   @ApiResponse(responseCode = "404", description = "Comentários não retornados porque o vídeo id é nulo")
   @ApiResponse(responseCode = "404", description = "Comentários não retornados porque o vídeo id não foi encontrado")
   @ApiResponse(responseCode = "500", description = "Erro no servidor")
    public List<CommentModel> findAllCommentsByVideo(@PathVariable(value = "idVideo")UUID idVideo){
        return commentService.findAllCommentsByVideo(idVideo);
   }

   @DeleteMapping(path = "api/v2/video/comment/{videoId}")
   @Operation(summary = "Endpoint para deletar um comentário", description = "Deleta o comentário através do id do vídeo")
   @ApiResponse(responseCode = "200", description = "Comentário foi deletado com sucesso")
   @ApiResponse(responseCode = "400", description = "Comentário não deletado porque o id do vídeo não é válido")
   @ApiResponse(responseCode = "403", description = "Comentário não foi deletado porque a autenticação do usuário falhou")
   @ApiResponse(responseCode = "500", description = "Erro no servidor")
    public ResponseEntity<String> deleteComment(@PathVariable(value = "videoId")UUID videoId){
       commentService.deleteCommentById(videoId);
       return ResponseEntity.ok().body("Comment successfully deleted");
   }
}
