package com.math012.social.video.videoplatformv2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.math012.social.video.videoplatformv2.DTO.VideoDTO;
import com.math012.social.video.videoplatformv2.config.thumbnailconfig.ThumbnailStorageConfigImpl;
import com.math012.social.video.videoplatformv2.config.videoconfig.VideoStorageConfigImpl;
import com.math012.social.video.videoplatformv2.exception.VideoStorageException;
import com.math012.social.video.videoplatformv2.model.UserModel;
import com.math012.social.video.videoplatformv2.model.VideoModel;
import com.math012.social.video.videoplatformv2.service.VideoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin("*")
@Tag(name = "VideoController", description = "Controlador para gerenciar os videos do usuário")
public class VideoController {

    @Autowired
    private VideoStorageConfigImpl videoStorage;

    @Autowired
    private ThumbnailStorageConfigImpl thumbnailStorage;

    @Autowired
    private VideoService videoService;

    @GetMapping("video/detail/{id}")
    @Operation(summary = "Endpoint para retornar um vídeo específico", description = "Retorna um vídeo através do id fornecido")
    @ApiResponse(responseCode = "200", description = "Vídeo retornado com sucesso")
    @ApiResponse(responseCode = "404", description = "Vídeo não retornado porque o id do vídeo não foi encontrado")
    @ApiResponse(responseCode = "404", description = "Vídeo não retornado porque o id do vídeo não foi encontrado")
    @ApiResponse(responseCode = "500", description = "Erro no servidor")
    public VideoModel findById(@PathVariable(value = "id")UUID id){
        return videoService.findById(id);
    }

    @GetMapping("videos/home")
    @Operation(summary = "Retorna uma lista de vídeos", description = "Retorna todos os vídeos registrados ")
    @ApiResponse(responseCode = "200", description = "Vídeos retornados com sucesso")
    @ApiResponse(responseCode = "500", description = "Erro no servidor")
    public List<VideoModel> findAllVideos(){
        return videoService.findAllVideos();
    }

    @GetMapping("videos/user/{username}")
    @Operation(summary = "Retorna todos os vídeos de um usuário", description = "Retorna todos os vídeos de um usuário específico através do seu username")
    @ApiResponse(responseCode = "200", description = "Vídeos retornados com sucesso")
    @ApiResponse(responseCode = "400", description = "Vídeos não retornados porque o username esta vazio")
    @ApiResponse(responseCode = "400", description = "Vídeos não retornados porque o username é nulo")
    @ApiResponse(responseCode = "404", description = "Vídeos não retornados porque o username não foi encontrado")
    @ApiResponse(responseCode = "500", description = "Erro no servidor")
    public List<VideoModel> findAllVideoByUsername(@PathVariable(value = "username")String username){
        return videoService.findAllVideosByUsername(username);
    }

    @GetMapping("videos/pageable")
    @Operation(summary = "Retorna uma lista de vídeos com paginação", description = "Retorna uma lista de vídeos com paginação definida através de parâmetros")
    @ApiResponse(responseCode = "200", description = "Vídeos retornados com sucesso")
    @ApiResponse(responseCode = "500", description = "Erro no servidor")
    public HashSet<VideoModel> findAllVideosWithPage(Pageable pageable){
        return videoService.findAllVideosWithPages(pageable);
    }

    @PostMapping("api/v2/post/video/{username}")
    @Operation(summary = "Realiza o envio de um vídeo", description = "Recebe um arquivo do tipo MP4 e realiza o upload do arquivo")
    @ApiResponse(responseCode = "200", description = "Vídeo enviado com sucesso")
    @ApiResponse(responseCode = "400", description = "Vídeos não enviado porque o username esta vazio")
    @ApiResponse(responseCode = "400", description = "Vídeos não enviado porque o username é nulo")
    @ApiResponse(responseCode = "400", description = "Vídeos não enviado porque o arquivo não é MP4")
    @ApiResponse(responseCode = "403", description = "Vídeos não foi enviado porque a autenticação do usuário falhou")
    @ApiResponse(responseCode = "404", description = "Vídeos não enviado porque o username não foi encontrado")
    @ApiResponse(responseCode = "500", description = "Erro no servidor")
    public VideoModel uploadVideo(MultipartHttpServletRequest request, @PathVariable(value = "username")String username) {
        try {
            var user = videoService.returnUserFromUsername(username);
            MultipartFile file = request.getFile("file");
            MultipartFile thumbnail = request.getFile("thumbnail");

            String dataVideo = request.getParameter("videoDTO");
            VideoDTO videoDTO = new ObjectMapper().readValue(dataVideo, VideoDTO.class);

            var videoPath = videoStorage.saveFile(file,username);

            String urlVideo = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("api/v2/post/video/download/")
                    .path(username)
                    .path("/")
                    .path(videoPath)
                    .toUriString();

            var video = new VideoModel(videoDTO.title(), videoDTO.description(), urlVideo,user, Instant.now());
            if (!(thumbnail == null)){
                var thumbnailPath = thumbnailStorage.saveFile(thumbnail,username);
                String urlThumbnail = ServletUriComponentsBuilder
                        .fromCurrentContextPath()
                        .path("api/v2/post/thumbnail/download/")
                        .path(username)
                        .path("/")
                        .path(thumbnailPath)
                        .toUriString();
                video.setThumbnail(urlThumbnail);
            }
            videoService.saveVideo(video);
            return video;
        }catch (Exception e){
            throw new VideoStorageException(e.getMessage());
        }
    }

    @GetMapping("api/v2/post/video/download/{username}/{path:.+}")
    @Operation(summary = "Faz o carregamento do vídeo de um usuário", description = "Realiza a busca do vídeo através de uma url e username")
    @ApiResponse(responseCode = "200", description = "Vídeo carregado com sucesso")
    @ApiResponse(responseCode = "400", description = "Vídeos não foi carregado")
    @ApiResponse(responseCode = "403", description = "Vídeos não foi carregado porque a autenticação do usuário falhou")
    @ApiResponse(responseCode = "404", description = "Vídeos não carregado porque o username não foi encontrado")
    @ApiResponse(responseCode = "500", description = "Erro no servidor")
    public ResponseEntity<Resource> download(@PathVariable(value = "username")String username, @PathVariable(value = "path") String path, HttpServletRequest request){
        Resource resource = videoStorage.loadFile(path,username);
        String content = null;

        try {
            content = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());

        }catch (Exception e){
            throw new VideoStorageException("download failed");
        }
        if (content == null){
            content = "application/octet-stream";
        }

        return ResponseEntity.ok().contentType(MediaType.parseMediaType(content)).header(HttpHeaders.CONTENT_DISPOSITION,"attachment; path=\""+resource.getFilename()+"\"").body(resource);
    }

    @GetMapping("api/v2/post/thumbnail/download/{username}/{path:.+}")
    @Operation(summary = "Faz o carregamento da thumbnail do usuário", description = "Realiza o carregamento da thumbnail enviada pelo usuário")
    @ApiResponse(responseCode = "200", description = "Thumbnail carregada com sucesso")
    @ApiResponse(responseCode = "400", description = "Thumbnail não foi carregada")
    @ApiResponse(responseCode = "403", description = "Thumbnail não foi carregadoa porque a autenticação do usuário falhou")
    @ApiResponse(responseCode = "404", description = "Thumbnail não carregada porque o username não foi encontrado")
    @ApiResponse(responseCode = "500", description = "Erro no servidor")
    public ResponseEntity<Resource> downloadThumbnail(@PathVariable(value = "username")String username, @PathVariable(value = "path") String path, HttpServletRequest request){
        Resource resource = thumbnailStorage.loadFile(path, username);
        String content = null;

        try {
            content = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        }catch (Exception e){
            throw new VideoStorageException("download failed");
        }
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(content)).header(HttpHeaders.CONTENT_DISPOSITION,"attachment; path=\""+resource.getFilename()+"\"").body(resource);
    }

    @DeleteMapping(path = "/api/v2/video/comment/delete/{videoId}")
    @Operation(summary = "Deleta um vídeo", description = "Deleta o vídeo especificado pelo ID e que tenha comentários")
    @ApiResponse(responseCode = "200", description = "Vídeo deletado com sucesso")
    @ApiResponse(responseCode = "403", description = "Vídeo não foi deletado porque a autenticação do usuário falhou")
    @ApiResponse(responseCode = "404", description = "Vídeo não foi deletado porque o id do vídeo não foi encontrado")
    @ApiResponse(responseCode = "500", description = "Erro no servidor")
    public ResponseEntity<String> deleteVideoWithComment(@PathVariable(value = "videoId")UUID videoId){
        videoService.deleteVideoWithComments(videoId);
        return ResponseEntity.ok("Your video was deleted");
    }

    @DeleteMapping(path = "/api/v2/video/{videoId}")
    @Operation(summary = "Deleta um vídeo", description = "Deleta o vídeo especificado pelo ID")
    @ApiResponse(responseCode = "200", description = "Vídeo deletado com sucesso")
    @ApiResponse(responseCode = "403", description = "Vídeo não foi deletado porque a autenticação do usuário falhou")
    @ApiResponse(responseCode = "404", description = "Vídeo não foi deletado porque o id do vídeo não foi encontrado")
    @ApiResponse(responseCode = "500", description = "Erro no servidor")
    public ResponseEntity<String> deleteVideoWithoutComments(@PathVariable(value = "videoId")UUID videoId){
        videoService.deleteVideoWithoutComments(videoId);
        return ResponseEntity.ok("Your video was deleted");
    }

    @GetMapping(path = "/video/username/videos/{videoId}")
    @Operation(summary = "Retorna um username do banco de dados", description = "Retorna um username com base no vídeo id")
    @ApiResponse(responseCode = "200", description = "Username retornado com sucesso")
    @ApiResponse(responseCode = "403", description = "Username não foi retornado porque a autenticação do usuário falhou")
    @ApiResponse(responseCode = "404", description = "Username não foi retornado porque o id do vídeo não foi encontrado")
    @ApiResponse(responseCode = "500", description = "Erro no servidor")
    public String findUsernameByVideoId(@PathVariable(value = "videoId")UUID videoId){
        return videoService.findUsernameByVideoId(videoId);
    }
}
