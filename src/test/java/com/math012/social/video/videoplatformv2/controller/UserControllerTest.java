package com.math012.social.video.videoplatformv2.controller;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.gson.Gson;
import com.math012.social.video.videoplatformv2.DTO.DescriptionDTO;
import com.math012.social.video.videoplatformv2.DTO.RegisterDTO;
import com.math012.social.video.videoplatformv2.DTO.UserDTO;
import com.math012.social.video.videoplatformv2.config.photoconfig.PhotoStorageConfigImpl;
import com.math012.social.video.videoplatformv2.config.security.TokenProvider;
import com.math012.social.video.videoplatformv2.exception.InvalidFieldsException;
import com.math012.social.video.videoplatformv2.exception.LoadVideoException;
import com.math012.social.video.videoplatformv2.exception.PhotoProfileException;
import com.math012.social.video.videoplatformv2.exception.UserNotFoundException;
import com.math012.social.video.videoplatformv2.model.UserModel;
import com.math012.social.video.videoplatformv2.model.VideoModel;
import com.math012.social.video.videoplatformv2.repository.UserRepository;
import com.math012.social.video.videoplatformv2.service.UserService;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;


import java.awt.color.ProfileDataException;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class UserControllerTest {

    // USER MODEL
    private static final UserModel USER = new UserModel("zoe","Lee","Zooe","12345");

    // LOGIN RESOURCE
    private static final UserDTO USER_DTO = new UserDTO("Zooe","12345");
    private static final UserDTO USER_DTO_NOT_REGISTERED = new UserDTO("AnnyLee","22222");
    private static final UserDTO USER_DTO_USERNAME_IS_BLANK = new UserDTO("","12345");
    private static final UserDTO USER_DTO_PASSWORD_IS_BLANK = new UserDTO("Zooe","");
    private static final UserDTO USER_DTO_USERNAME_NULL = new UserDTO(null,"12345");
    private static final UserDTO USER_DTO_PASSWORD_NULL = new UserDTO("Zooe",null);
    // REGISTER RESOURCE
    private static final URI LOCATION = URI.create("user/1");
    private static final RegisterDTO REGISTER_DTO = new RegisterDTO("Shawn", "Tzui","ShawnT","1234");
    // VALIDATION FOR NULL FIELDS
    private static final RegisterDTO REGISTER_DTO_NAME_IS_NULL = new RegisterDTO(null, "Tzui","ShawnT","1234");
    private static final RegisterDTO REGISTER_DTO_LASTNAME_IS_NULL = new RegisterDTO("Shawn", null,"ShawnT","1234");
    private static final RegisterDTO REGISTER_DTO_USERNAME_IS_NULL = new RegisterDTO("Shawn", "Tzui",null,"1234");
    private static final RegisterDTO REGISTER_DTO_PASSWORD_IS_NULL = new RegisterDTO("Shawn", "Tzui","ShawnT",null);
    // VALIDATION FOR BLANK/EMPTY FIELDS
    private static final RegisterDTO REGISTER_DTO_NAME_IS_BLANK = new RegisterDTO("", "Tzui","ShawnT","1234");
    private static final RegisterDTO REGISTER_DTO_LASTNAME_IS_BLANK = new RegisterDTO("Shawn", "","ShawnT","1234");
    private static final RegisterDTO REGISTER_DTO_USERNAME_IS_BLANK = new RegisterDTO("Shawn", "Tzui","","1234");
    private static final RegisterDTO REGISTER_DTO_PASSWORD_IS_BLANK = new RegisterDTO("Shawn", "Tzui","ShawnT","");
    // UPLOAD PHOTO RESOURCE
    private static final MockMultipartFile FILE = new MockMultipartFile("file","filename.png","contentType",new byte[0]);
    private static final MockMultipartFile FILE_INVALID = new MockMultipartFile("file","filename.mp4","contentType",new byte[0]);
    private static final MockMultipartFile FILE_INVALID_GENERIC_EXCEPTION = new MockMultipartFile("file",null,"contentType",new byte[0]);
    // DESCRIPTION RESOURCE
    private static final DescriptionDTO DESCRIPTION_DTO = new DescriptionDTO("My description");
    private static final DescriptionDTO DESCRIPTION_DTO_BLANK = new DescriptionDTO("");
    private static final DescriptionDTO DESCRIPTION_DTO_NULL = new DescriptionDTO(null);

    // SECURITY
    Algorithm algorithm = Algorithm.HMAC256("12345");
    Date date = new Date();
    long validationTime = 18000000;
    String TOKEN_JWT = JWT.create()
            .withIssuer("auth")
            .withSubject("Zooe")
            .withExpiresAt(new Date(date.getTime() + validationTime))
            .sign(algorithm);

    @Autowired
    UserController userController;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @MockBean
    PhotoStorageConfigImpl photoStorageConfig;

    @MockBean
    TokenProvider tokenProvider;

    @MockBean
    AuthenticationManager authenticationManager;


    @MockBean
    UserRepository userRepository;

    @MockBean
    private FilterChain filterChain;

    @BeforeEach
    void setup() throws MalformedURLException {
        ResponseEntity<String> token = ResponseEntity.ok(TOKEN_JWT);


        // LOGIN TEST
        when(userService.login(USER_DTO)).thenReturn(token);
        when(userService.login(USER_DTO_USERNAME_IS_BLANK)).thenThrow(new InvalidFieldsException("Error, your fields is blank or empty, please try again"));
        when(userService.login(USER_DTO_PASSWORD_IS_BLANK)).thenThrow(new InvalidFieldsException("Error, your fields is blank or empty, please try again"));
        when(userService.login(USER_DTO_USERNAME_NULL)).thenThrow(new InvalidFieldsException("Error, your fields is null, please try again"));
        when(userService.login(USER_DTO_PASSWORD_NULL)).thenThrow(new InvalidFieldsException("Error, your fields is null, please try again"));
        when(userService.login(USER_DTO_NOT_REGISTERED)).thenThrow(new UserNotFoundException("Your credentials are not valid, try again"));

        // REGISTER TEST
        when(userService.register(REGISTER_DTO)).thenReturn(ResponseEntity.created(LOCATION).build());
        when(userService.register(REGISTER_DTO_NAME_IS_NULL)).thenThrow(new InvalidFieldsException("Error, your fields is null, please try again"));
        when(userService.register(REGISTER_DTO_LASTNAME_IS_NULL)).thenThrow(new InvalidFieldsException("Error, your fields is null, please try again"));
        when(userService.register(REGISTER_DTO_USERNAME_IS_NULL)).thenThrow(new InvalidFieldsException("Error, your fields is null, please try again"));
        when(userService.register(REGISTER_DTO_PASSWORD_IS_NULL)).thenThrow(new InvalidFieldsException("Error, your fields is null, please try again"));

        when(userService.register(REGISTER_DTO_NAME_IS_BLANK)).thenThrow(new InvalidFieldsException("Error, your fields is blank or empty, please try again"));
        when(userService.register(REGISTER_DTO_LASTNAME_IS_BLANK)).thenThrow(new InvalidFieldsException("Error, your fields is blank or empty, please try again"));
        when(userService.register(REGISTER_DTO_USERNAME_IS_BLANK)).thenThrow(new InvalidFieldsException("Error, your fields is blank or empty, please try again"));
        when(userService.register(REGISTER_DTO_PASSWORD_IS_BLANK)).thenThrow(new InvalidFieldsException("Error, your fields is blank or empty, please try again"));

        // UPLOAD PHOTO TEST
        when(photoStorageConfig.saveFile(FILE,"Zooe")).thenReturn(FILE.getOriginalFilename());
        when(photoStorageConfig.saveFile(FILE_INVALID,"Zooe")).thenThrow(new PhotoProfileException("Video upload is not supported for profile photos"));
        when(userService.returnUserFromUsername("Law")).thenThrow(new UserNotFoundException("This username: Law, not found"));
        when(photoStorageConfig.saveFile(FILE_INVALID_GENERIC_EXCEPTION,"Zooe")).thenThrow(new PhotoProfileException("Your photo was not uploaded, please try again"));
        // FIND ALL VIDEOS BY USERNAME TEST
        List<VideoModel> videos = List.of(
                new VideoModel("title 1","description 1","url 1",USER, Instant.now())
                ,new VideoModel("title 2","description 2","url 2",USER, Instant.now())
                ,new VideoModel("title 3","description 3","url 3",USER, Instant.now())
                ,new VideoModel("title 4","description 4","url 4",USER, Instant.now()));

        when(userService.findAllVideosByUsername("Zooe")).thenReturn(ResponseEntity.ok(videos));
        when(userService.findAllVideosByUsername("Law")).thenThrow(new LoadVideoException("The user's Law videos could not be loaded, please try again"));
        // FIND USERS BY USERNAME TEST
        when(userService.findUserByUsername("Zooe")).thenReturn(ResponseEntity.ok(USER));
        when(userService.findUserByUsername("Law")).thenThrow(new UserNotFoundException("Username Law not found, try again"));

        // CHANGE DESCRIPTION TEST
        when(userService.changeTheUserDescription(DESCRIPTION_DTO.description(),"Zooe")).thenReturn(ResponseEntity.ok("Description has been changed"));
        when(userService.changeTheUserDescription(DESCRIPTION_DTO_BLANK.description(),"Zooe")).thenThrow(new InvalidFieldsException("Error, your fields is blank or empty, please try again"));
        when(userService.changeTheUserDescription(DESCRIPTION_DTO_NULL.description(),"Zooe")).thenThrow(new InvalidFieldsException("Error, your fields is null, please try again"));
        when(userService.changeTheUserDescription(DESCRIPTION_DTO.description(),"Law")).thenThrow(new UserNotFoundException("Username not found, try again"));
        // ALL CHANNELS TEST
        var listOfUsers = List.of(
                new UserModel("Bob","Lau","Zooe","12345")
                ,new UserModel("Anny","Brian","Zoee","12345")
        );
        when(userService.findAllUsers()).thenReturn(ResponseEntity.ok(listOfUsers));

        // SECURITY
        UsernamePasswordAuthenticationToken authenticate =  new UsernamePasswordAuthenticationToken(USER_DTO.username(), USER_DTO.password());
        Authentication authenticated = new UsernamePasswordAuthenticationToken(USER, null, new ArrayList<>());

        when(userRepository.findByUsername("Zooe")).thenReturn(Optional.of(USER));
        when(userService.returnUserFromUsername("Zooe")).thenReturn(USER);
        when(authenticationManager.authenticate(authenticate)).thenReturn(authenticated);
        when(tokenProvider.tokenProvider(USER)).thenReturn(String.valueOf(token));

    }

    @Test
    void loginSuccess() throws Exception {
        var json = new Gson().toJson(USER_DTO);
        mockMvc.perform(post("/v2/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.valueOf(json)).characterEncoding("utf-8"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(TOKEN_JWT,result.getResponse().getContentAsString()));
    }

    @Test
    void loginFailedWhenTheUsernameIsNull() throws Exception {
        var json = new Gson().toJson(USER_DTO_USERNAME_NULL);
        mockMvc.perform(post("/v2/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(json)).characterEncoding("utf-8"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof InvalidFieldsException))
                .andExpect(jsonPath("$.msg").value("Error, your fields is null, please try again"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void loginFailedWhenPasswordIsNull() throws Exception {
        var json = new Gson().toJson(USER_DTO_PASSWORD_NULL);
        mockMvc.perform(post("/v2/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(json)).characterEncoding("utf-8"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof InvalidFieldsException))
                .andExpect(jsonPath("$.msg").value("Error, your fields is null, please try again"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void loginFailedWhenTheUsernameIsBlank() throws Exception {
        var json = new Gson().toJson(USER_DTO_USERNAME_IS_BLANK);
        mockMvc.perform(post("/v2/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.valueOf(json)).characterEncoding("utf-8"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof InvalidFieldsException))
                .andExpect(jsonPath("$.msg").value("Error, your fields is blank or empty, please try again"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void loginFailedWhenThePasswordIsBlank() throws Exception {
        var json = new Gson().toJson(USER_DTO_PASSWORD_IS_BLANK);
        mockMvc.perform(post("/v2/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(json)).characterEncoding("utf-8"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof InvalidFieldsException))
                .andExpect(jsonPath("$.msg").value("Error, your fields is blank or empty, please try again"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void loginFailedWhenTheUsernameIsNotRegistered() throws Exception {
        var json = new Gson().toJson(USER_DTO_NOT_REGISTERED);
        mockMvc.perform(post("/v2/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(json)).characterEncoding("utf-8"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserNotFoundException))
                .andExpect(jsonPath("$.msg").value("Your credentials are not valid, try again"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void registerSuccess() throws Exception {
        var json = new Gson().toJson(REGISTER_DTO);
        mockMvc.perform(post("/v2/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.valueOf(json)).characterEncoding("utf-8"))
                .andExpect(status().isCreated())
                .andExpect(result -> assertEquals(json,result.getRequest().getContentAsString()));
    }

    @Test
    void registerFailedWhenTheNameIsNull() throws Exception {
        var json = new Gson().toJson(REGISTER_DTO_NAME_IS_NULL);
        mockMvc.perform(post("/v2/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(json)).characterEncoding("utf-8"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof InvalidFieldsException))
                .andExpect(jsonPath("$.msg").value("Error, your fields is null, please try again"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void registerFailedWhenTheLastnameIsNull() throws Exception {
        var json = new Gson().toJson(REGISTER_DTO_LASTNAME_IS_NULL);
        mockMvc.perform(post("/v2/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(json)).characterEncoding("utf-8"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof InvalidFieldsException))
                .andExpect(jsonPath("$.msg").value("Error, your fields is null, please try again"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void registerFailedWhenTheUsernameIsNull() throws Exception {
        var json = new Gson().toJson(REGISTER_DTO_USERNAME_IS_NULL);
        mockMvc.perform(post("/v2/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(json)).characterEncoding("utf-8"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof InvalidFieldsException))
                .andExpect(jsonPath("$.msg").value("Error, your fields is null, please try again"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void registerFailedWhenThePasswordIsNull() throws Exception {
        var json = new Gson().toJson(REGISTER_DTO_PASSWORD_IS_NULL);
        mockMvc.perform(post("/v2/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(json)).characterEncoding("utf-8"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof InvalidFieldsException))
                .andExpect(jsonPath("$.msg").value("Error, your fields is null, please try again"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void registerFailedWhenTheNameIsBlank() throws Exception {
        var json = new Gson().toJson(REGISTER_DTO_NAME_IS_BLANK);
        mockMvc.perform(post("/v2/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(json)).characterEncoding("utf-8"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof InvalidFieldsException))
                .andExpect(jsonPath("$.msg").value("Error, your fields is blank or empty, please try again"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void registerFailedWhenTheLastnameIsBlank() throws Exception {
        var json = new Gson().toJson(REGISTER_DTO_LASTNAME_IS_BLANK);
        mockMvc.perform(post("/v2/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(json)).characterEncoding("utf-8"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof InvalidFieldsException))
                .andExpect(jsonPath("$.msg").value("Error, your fields is blank or empty, please try again"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void registerFailedWhenTheUsernameIsBlank() throws Exception {
        var json = new Gson().toJson(REGISTER_DTO_USERNAME_IS_BLANK);
        mockMvc.perform(post("/v2/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(json)).characterEncoding("utf-8"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof InvalidFieldsException))
                .andExpect(jsonPath("$.msg").value("Error, your fields is blank or empty, please try again"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void registerFailedWhenThePasswordIsBlank() throws Exception {
        var json = new Gson().toJson(REGISTER_DTO_PASSWORD_IS_BLANK);
        mockMvc.perform(post("/v2/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(json)).characterEncoding("utf-8"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof InvalidFieldsException))
                .andExpect(jsonPath("$.msg").value("Error, your fields is blank or empty, please try again"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void uploadPhotoProfileSuccess() throws Exception {
        mockMvc.perform(multipart("/api/v2/post/photo/profile/{username}", "Zooe")
                        .file(FILE)
                        .header("Authorization",TOKEN_JWT)
                .contentType(MediaType.MULTIPART_FORM_DATA).characterEncoding("utf-8")
        )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void uploadPhotoProfileFailedWhenTheFormatOfFileIsInvalid() throws Exception {
        mockMvc.perform(multipart("/api/v2/post/photo/profile/{username}", "Zooe")
                        .file(FILE_INVALID)
                        .header("Authorization",TOKEN_JWT)
                        .contentType(MediaType.MULTIPART_FORM_DATA).characterEncoding("utf-8")
                )
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof PhotoProfileException))
                .andExpect(jsonPath("$.msg").value("Video upload is not supported for profile photos"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void uploadPhotoProfileFailedWhenTheUsernameIsNotRegistered() throws Exception {
        mockMvc.perform(multipart("/api/v2/post/photo/profile/{username}", "Law")
                        .file(FILE)
                        .header("Authorization",TOKEN_JWT)
                        .contentType(MediaType.MULTIPART_FORM_DATA).characterEncoding("utf-8")
                )
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserNotFoundException))
                .andExpect(jsonPath("$.msg").value("This username: Law, not found"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void loadPhotoProfileSuccess() throws Exception {
        Resource resource = mock(UrlResource.class);
        when(resource.getFile()).thenReturn(new File("mocked-file.png"));
        when(photoStorageConfig.loadFile("filename.png","Zooe")).thenReturn(resource);
        when(photoStorageConfig.loadFile(FILE.getOriginalFilename(),"Zooe")).thenReturn(resource);
        mockMvc.perform(get("/api/v2/post/photo/download/{username}/{path}", "Zooe", "filename.png")
                        .header("Authorization",TOKEN_JWT)
                        .header("CONTENT_DISPOSITION","attachment; path=\""+resource.getFilename())
                        .contentType(MediaType.APPLICATION_OCTET_STREAM).characterEncoding("utf-8"))
                .andDo(print());
    }

    @Test
    void findAllVideosByUsernameSuccess() throws Exception {
        mockMvc.perform(get("/all/videos/{username}","Zooe")
                .header("Authorization",TOKEN_JWT)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").exists())
                .andExpect(jsonPath("$[0].description").exists())
                .andExpect(jsonPath("$[0].url").exists())
                .andExpect(jsonPath("$[0].date").exists());
    }

    @Test
    void findAllVideosByUsernameFailedWhenUsernameIsNotFound() throws Exception {
        mockMvc.perform(get("/all/videos/{username}","Law")
                        .header("Authorization",TOKEN_JWT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof LoadVideoException))
                .andExpect(jsonPath("$.msg").value("The user's Law videos could not be loaded, please try again"))
                .andExpect(jsonPath("$.timestamp").exists());

    }

    @Test
    void changeTheUserDescriptionSuccess() throws Exception {
        var json = new Gson().toJson(DESCRIPTION_DTO);
        mockMvc.perform(post("/change/description/user/{username}","Zooe")
                        .header("Authorization",TOKEN_JWT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.valueOf(json)).characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(result.getResponse().getContentAsString(),"Description has been changed"));
    }

    @Test
    void changeTheUserDescriptionFailedWhenDescriptionIsBlankOrEmpty() throws Exception {
        var json = new Gson().toJson(DESCRIPTION_DTO_BLANK);
        mockMvc.perform(post("/change/description/user/{username}","Zooe")
                        .header("Authorization",TOKEN_JWT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(json)).characterEncoding("utf-8"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof InvalidFieldsException))
                .andExpect(jsonPath("$.msg").value("Error, your fields is blank or empty, please try again"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void changeTheUserDescriptionFailedWhenDescriptionIsNull() throws Exception {
        var json = new Gson().toJson(DESCRIPTION_DTO_NULL);
        mockMvc.perform(post("/change/description/user/{username}","Zooe")
                        .header("Authorization",TOKEN_JWT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(json)).characterEncoding("utf-8"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof InvalidFieldsException))
                .andExpect(jsonPath("$.msg").value("Error, your fields is null, please try again"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void changeTheUserDescriptionFailedWhenUsernameIsNotRegistered() throws Exception {
        var json = new Gson().toJson(DESCRIPTION_DTO);
        mockMvc.perform(post("/change/description/user/{username}","Law")
                        .header("Authorization",TOKEN_JWT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(json)).characterEncoding("utf-8"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserNotFoundException))
                .andExpect(jsonPath("$.msg").value("Username not found, try again"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void findAllUsersSuccess() throws Exception {
        mockMvc.perform(get("/all/channels")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andDo(print());
    }

    @Test
    void findUserByUsernameSuccess() throws Exception {
        mockMvc.perform(get("/user/info/{username}","Zooe")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void findUserByUsernameFailedWhenUsernameNotRegistered() throws Exception {
        mockMvc.perform(get("/user/info/{username}","Law")
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserNotFoundException))
                .andExpect(jsonPath("$.msg").value("Username Law not found, try again"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
