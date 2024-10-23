package com.math012.social.video.videoplatformv2.controller;

import com.math012.social.video.videoplatformv2.DTO.DescriptionDTO;
import com.math012.social.video.videoplatformv2.DTO.RegisterDTO;
import com.math012.social.video.videoplatformv2.DTO.UserDTO;
import com.math012.social.video.videoplatformv2.config.photoconfig.PhotoStorageConfigImpl;
import com.math012.social.video.videoplatformv2.exception.VideoStorageException;
import com.math012.social.video.videoplatformv2.model.UserModel;
import com.math012.social.video.videoplatformv2.model.VideoModel;
import com.math012.social.video.videoplatformv2.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@CrossOrigin("*")
@Tag(name = "UserController", description = "Controlador para autenticação e dados de usuários")
@SecurityRequirement(name = "Application Security")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private PhotoStorageConfigImpl photoStorage;

    @PostMapping("/v2/auth/login")
    @Operation(summary = "Endpoint para realizar o login do usuário", description = "faz uma autenticação através do username e password, retornando um bearer token")
    @ApiResponse(responseCode = "200", description = "Usuário autenticado e o token é retornado")
    @ApiResponse(responseCode = "400", description = "Credenciais de usuário inválidas")
    @ApiResponse(responseCode = "500", description = "Erro no servidor")
    public ResponseEntity<String> login(@Valid @RequestBody UserDTO userDTO){
        return userService.login(userDTO);
    }

    @PostMapping("/v2/auth/register")
    @Operation(summary = "Endpoint para registrar um usuário", description = "Recebe os dados do usuário e salva no banco de dados ")
    @ApiResponse(responseCode = "201", description = "Usuário registrado com sucesso")
    @ApiResponse(responseCode = "400", description = "Usuário não registrado porque suas credenciais estão vazias")
    @ApiResponse(responseCode = "400", description = "Usuário não registrado porque suas credenciais estão nulas")
    @ApiResponse(responseCode = "500", description = "Erro no servidor")
    public ResponseEntity<String> register(@RequestBody RegisterDTO registerDTO){
        return userService.register(registerDTO);
    }

    @PostMapping("api/v2/post/photo/profile/{username}")
    @Operation(summary = "Endpoint para enviar uma foto de perfil", description = "Realiza o envio de uma foto para o perfil do usuário")
    @ApiResponse(responseCode = "200", description = "A foto foi enviada com sucesso")
    @ApiResponse(responseCode = "400", description = "A foto não foi enviada porque o usuário tentou enviar um arquivo .MP4")
    @ApiResponse(responseCode = "403", description = "A foto não foi enviada porque a autenticação do usuário falhou")
    @ApiResponse(responseCode = "404", description = "A foto não foi enviado porque o nome de usuário não foi encontrado")
    @ApiResponse(responseCode = "500", description = "Erro no servidor")
    public UserModel uploadPhotoProfile(MultipartHttpServletRequest request, @PathVariable(value = "username")String username){
        MultipartFile file = request.getFile("file");
        var user = userService.returnUserFromUsername(username);
        var photoPath = photoStorage.saveFile(file,username);
        String urlPhoto = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("api/v2/post/photo/download/")
                .path(username)
                .path("/")
                .path(photoPath)
                .toUriString();
        user.setProfilePhoto(urlPhoto);
        userService.saveUser(user);
        return user;
    }

    @GetMapping("api/v2/post/photo/download/{username}/{path:.+}")
    @Operation(summary = "Endpoint para retornar a foto de perfil", description = "Retorna a foto de perfil de um usuário em específico através de um path")
    @ApiResponse(responseCode = "200", description = "Foto carregada com sucesso")
    @ApiResponse(responseCode = "400", description = "Não foi possível carregar a foto de perfil")
    @ApiResponse(responseCode = "403", description = "A foto não foi carregada porque a autenticação do usuário falhou")
    @ApiResponse(responseCode = "500", description = "Erro no servidor")
    public ResponseEntity<Resource> download(@PathVariable(value = "username")String username, @PathVariable(value = "path") String path, HttpServletRequest request){
        Resource resource = photoStorage.loadFile(path, username);
        String content = null;

        try {
            content = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        }catch (Exception e){
            throw new VideoStorageException(e.getMessage());
        }
        if (content == null){
            content = "application/octet-stream";
        }
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(content)).header(HttpHeaders.CONTENT_DISPOSITION,"attachment; path=\""+resource.getFilename()+"\"").body(resource);
    }

    @GetMapping("/all/videos/{username}")
    @Operation(summary = "Endpoint para retornar todos os vídeos de um usuário", description = "Retorna todos os vídeos de um usuário através de seu username")
    @ApiResponse(responseCode = "200", description = "Todos os vídeos do usuário retornado com sucesso")
    @ApiResponse(responseCode = "404", description = "Nenhum vídeo retornado porque o username do usuário não foi encontrado")
    @ApiResponse(responseCode = "500", description = "Erro no servidor")
    public ResponseEntity<List<VideoModel>> findAllVideosByUsername(@PathVariable(value = "username")String username){
        return userService.findAllVideosByUsername(username);
    }

    @PostMapping("/change/description/user/{username}")
    @Operation(summary = "Endpoint para mudar a descrição do usuário", description = "O usuário pode alterar sua descrição")
    @ApiResponse(responseCode = "200", description = "Descrição do usuário alterada com sucesso")
    @ApiResponse(responseCode = "400", description = "Descrição não alterada porque os campos estão vazios")
    @ApiResponse(responseCode = "400", description = "Descrição não alterada porque os campos estão nulos")
    @ApiResponse(responseCode = "403", description = "Descrição não alterada porque a autenticação do usuário falhou")
    @ApiResponse(responseCode = "404", description = "Descrição não alterada porque o username do usuário não foi encontrado")
    @ApiResponse(responseCode = "500", description = "Erro no servidor")
    public ResponseEntity<String> changeTheUserDescription(@PathVariable(value = "username")String username, @RequestBody DescriptionDTO descriptionDTO){
        return userService.changeTheUserDescription(descriptionDTO.description(), username);
    }

    @GetMapping("/all/channels")
    @Operation(summary = "Endpoint para recuperar todos os usuários", description = "Recupera todos os usuários cadastrados")
    @ApiResponse(responseCode = "200", description = "Usuários retornados com sucesso")
    @ApiResponse(responseCode = "500", description = "Erro no servidor")
    public ResponseEntity<List<UserModel>> findAllUsers(){
        return userService.findAllUsers();
    }

    @GetMapping("/user/info/{username}")
    @Operation(summary = "Endpoint para encontrar um usuário", description = "Recupera um usuário com base em seu username")
    @ApiResponse(responseCode = "200", description = "Usuário retornado com sucesso")
    @ApiResponse(responseCode = "404", description = "Usuário não retornado porque o seu username não foi encontrado")
    @ApiResponse(responseCode = "500", description = "Erro no servidor")
    public ResponseEntity<UserModel> findUserByUsername(@PathVariable(value = "username")String username){
        return userService.findUserByUsername(username);
    }
}
