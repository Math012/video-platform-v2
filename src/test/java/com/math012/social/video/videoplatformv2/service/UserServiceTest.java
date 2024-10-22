package com.math012.social.video.videoplatformv2.service;
import static org.junit.jupiter.api.Assertions.*;

import com.math012.social.video.videoplatformv2.DTO.RegisterDTO;
import com.math012.social.video.videoplatformv2.DTO.UserDTO;
import com.math012.social.video.videoplatformv2.config.security.TokenProvider;
import com.math012.social.video.videoplatformv2.exception.*;
import com.math012.social.video.videoplatformv2.model.UserModel;
import com.math012.social.video.videoplatformv2.model.VideoModel;
import com.math012.social.video.videoplatformv2.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.Instant;
import java.util.*;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    UserService userService;

    @MockBean
    UserRepository userRepository;

    @MockBean
    TokenProvider tokenProvider;

    @MockBean
    AuthenticationManager authenticationManager;

    private static final String USERNAME = "bobLau";
    private static final String USERNAME_REGISTER = "AnnyBrian";
    private static final String PASSWORD = "12345";
    private static final UserDTO USER_DTO = new UserDTO(USERNAME,PASSWORD);
    private static final UserDTO USER_DTO_NOT_REGISTERED = new UserDTO("Kay","123467989");
    private static final RegisterDTO USER_REGISTER_DTO = new RegisterDTO("Anny", "Brian",USERNAME_REGISTER,PASSWORD);
    private static final RegisterDTO USER_REGISTER_DTO_USERNAME_ALREADY_REGISTERED = new RegisterDTO("Anny", "Brian",USERNAME,PASSWORD);



    @BeforeEach
    void setup(){
        var user = new UserModel("Bob","Lau",USERNAME,PASSWORD);
        var userEncrypted = new UserModel("Anny","Brian",USERNAME_REGISTER,PASSWORD);
        var listOfVideos = List.of(
                new VideoModel("video mock","description mock","url mock",user, Instant.now())
                ,new VideoModel("video mock 2","description mock 2","url mock 2",user, Instant.now())
        );
        var listOfUsers = List.of(
                new UserModel("Bob","Lau",USERNAME,PASSWORD)
                ,new UserModel("Anny","Brian",USERNAME_REGISTER,PASSWORD)
        );

        // login()
        UsernamePasswordAuthenticationToken authenticate =  new UsernamePasswordAuthenticationToken(USER_DTO.username(), USER_DTO.password());
        Authentication authenticated = new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());

        // Register()
        when(userRepository.save(userEncrypted)).thenReturn(userEncrypted);

        // loadUserByUsername()
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));

        // login()
        when(authenticationManager.authenticate(authenticate)).thenReturn(authenticated);
        when(tokenProvider.tokenProvider(user)).thenReturn("token");

        // findAllVideosByUsername()
        when(userRepository.findAllVideosByUsername(USERNAME)).thenReturn(listOfVideos);

        // findAllUsers()
        when(userRepository.findAll()).thenReturn(listOfUsers);
    }

    @Test
    void loadUserByUsernameWhenUsernameAndPasswordExists(){
        UserDetails user = userService.loadUserByUsername("bobLau");
        assertEquals(USERNAME,user.getUsername());
        assertEquals(PASSWORD,user.getPassword());
        verify(userRepository).findByUsername(USERNAME);
    }

    @Test
    void loadUserByUsernameWhenUsernameNotFound(){
         Exception exception = assertThrows(UsernameNotFoundException.class, ()->{
             userService.loadUserByUsername("Anne");
         });
         assertEquals("User not found with username: Anne", exception.getMessage());
    }

    @Test
    void loginSuccessfullyWhenUsernameAndPasswordExist(){
        assertEquals("token", userService.login(USER_DTO).getBody());
    }

    @Test
    void loginFailedWhenUsernameIsNull(){
        UserDTO userDTONull = new UserDTO(null,"1234");
        Exception exception = assertThrows(InvalidFieldsException.class, () ->{
           userService.login(userDTONull);
        });
        assertEquals("Error, your fields is null, please try again", exception.getMessage());
    }

    @Test
    void loginFailedWhenPasswordIsNull(){
        UserDTO userDTONull = new UserDTO("1234",null);
        Exception exception = assertThrows(InvalidFieldsException.class, () ->{
            userService.login(userDTONull);
        });
        assertEquals("Error, your fields is null, please try again", exception.getMessage());
    }

    @Test
    void loginFailedWhenUsernameIsBlankOrEmpty(){
        UserDTO userDTONull = new UserDTO("","12312");
        Exception exception = assertThrows(InvalidFieldsException.class, () ->{
            userService.login(userDTONull);
        });
        assertEquals("Error, your fields is blank or empty, please try again", exception.getMessage());
    }

    @Test
    void loginFailedWhenPasswordIsBlankOrEmpty(){
        UserDTO userDTONull = new UserDTO("Math","");
        Exception exception = assertThrows(InvalidFieldsException.class, () ->{
            userService.login(userDTONull);
        });
        assertEquals("Error, your fields is blank or empty, please try again", exception.getMessage());
    }

    @Test
    void loginFailedWhenTheCredentialsNotRegistered(){
        Exception exception = assertThrows(UserNotFoundException.class, () ->{
            userService.login(USER_DTO_NOT_REGISTERED);
        });
        assertEquals("Your credentials are not valid, try again", exception.getMessage());
    }

    @Test
    void registerSuccess(){
        var user = userService.register(USER_REGISTER_DTO);
        assertEquals(HttpStatus.CREATED,user.getStatusCode());
    }



    @Test
    void registerFailedWhenTheNameIsNull(){
        RegisterDTO userRegister = new RegisterDTO(null, "test","test","test");
        Exception exception = assertThrows(InvalidFieldsException.class, () ->{
            userService.register(userRegister);
        });
        assertEquals("Error, your fields is null, please try again", exception.getMessage());
    }

    @Test
    void registerFailedWhenTheLastnameIsNull(){
        RegisterDTO userRegister = new RegisterDTO("test", null,"test","test");
        Exception exception = assertThrows(InvalidFieldsException.class, () ->{
            userService.register(userRegister);
        });
        assertEquals("Error, your fields is null, please try again", exception.getMessage());
    }

    @Test
    void registerFailedWhenTheUsernameIsNull(){
        RegisterDTO userRegister = new RegisterDTO("test", "test",null,"test");
        Exception exception = assertThrows(InvalidFieldsException.class, () ->{
            userService.register(userRegister);
        });
        assertEquals("Error, your fields is null, please try again", exception.getMessage());
    }

    @Test
    void registerFailedWhenThePasswordIsNull(){
        RegisterDTO userRegister = new RegisterDTO("test", "test","test",null);
        Exception exception = assertThrows(InvalidFieldsException.class, () ->{
            userService.register(userRegister);
        });
        assertEquals("Error, your fields is null, please try again", exception.getMessage());
    }


    @Test
    void registerFailedWhenTheNameIsBlankOrEmpty(){
        RegisterDTO userRegister = new RegisterDTO("", "test","test","test");
        Exception exception = assertThrows(InvalidFieldsException.class, () ->{
            userService.register(userRegister);
        });
        assertEquals("Error, your fields is blank or empty, please try again", exception.getMessage());
    }

    @Test
    void registerFailedWhenTheLastnameIsBlankOrEmpty(){
        RegisterDTO userRegister = new RegisterDTO("test", "","test","test");
        Exception exception = assertThrows(InvalidFieldsException.class, () ->{
            userService.register(userRegister);
        });
        assertEquals("Error, your fields is blank or empty, please try again", exception.getMessage());
    }

    @Test
    void registerFailedWhenTheUsernameIsBlankOrEmpty(){
        RegisterDTO userRegister = new RegisterDTO("test", "test","","test");
        Exception exception = assertThrows(InvalidFieldsException.class, () ->{
            userService.register(userRegister);
        });
        assertEquals("Error, your fields is blank or empty, please try again", exception.getMessage());
    }

    @Test
    void registerFailedWhenThePasswordIsBlankOrEmpty(){
        RegisterDTO userRegister = new RegisterDTO("test", "test","","test");
        Exception exception = assertThrows(InvalidFieldsException.class, () ->{
            userService.register(userRegister);
        });
        assertEquals("Error, your fields is blank or empty, please try again", exception.getMessage());
    }


    @Test
    void registerFailedTheUsernameAlreadyRegistered(){
        Exception exception = assertThrows(SecurityAuthenticationException.class, () ->{
            userService.register(USER_REGISTER_DTO_USERNAME_ALREADY_REGISTERED);
        });
        assertEquals("This username: " + USERNAME + " already registered", exception.getMessage());
    }

    @Test
    void findAllVideosByUsernameSuccess(){
        var videos = userService.findAllVideosByUsername(USERNAME);
        assertEquals(2, Objects.requireNonNull(videos.getBody()).size());
        assertEquals("bobLau",videos.getBody().get(0).getUser().getUsername());
        assertEquals("bobLau",videos.getBody().get(1).getUser().getUsername());
    }

    @Test
    void findAllVideosByUsernameFailedWhenTheUsernameIsEmpty(){
        String username = "";
        Exception exception = assertThrows(LoadVideoException.class, () ->{
            userService.findAllVideosByUsername(username);
        });
        assertEquals("The user's  videos could not be loaded, please try again", exception.getMessage());
    }

    @Test
    void findAllVideosByUsernameFailedWhenTheUsernameIsNotRegistered(){
        Exception exception = assertThrows(LoadVideoException.class, () ->{
           userService.findAllVideosByUsername(USERNAME_REGISTER);
        });
        assertEquals("The user's AnnyBrian videos could not be loaded, please try again", exception.getMessage());
    }

    @Test
    void changeTheUserDescriptionSuccess(){
        String description = "user description";
        String response = userService.changeTheUserDescription(description,USERNAME).getBody();
        assertEquals("Description has been changed", response);
    }
    @Test
    void changeTheUserDescriptionFailedWhenUsernameIsNotRegistered(){
        String description = "user description";
        Exception exception = assertThrows(UserNotFoundException.class, () ->{
            userService.changeTheUserDescription(description, USERNAME_REGISTER);
        });
        assertEquals("Username not found, try again",exception.getMessage());
    }

    @Test
    void changeTheUserDescriptionFailedWhenTheDescriptionIsNull(){
        Exception exception = assertThrows(InvalidFieldsException.class, () ->{
            userService.changeTheUserDescription(null, USERNAME);
        });
        assertEquals("Error, your fields is null, please try again",exception.getMessage());
    }

    @Test
    void changeTheUserDescriptionFailedWhenTheUsernameIsNull(){
        Exception exception = assertThrows(InvalidFieldsException.class, () ->{
            userService.changeTheUserDescription("test", null);
        });
        assertEquals("Error, your fields is null, please try again",exception.getMessage());
    }

    @Test
    void changeTheUserDescriptionFailedWhenTheDescriptionIsBlank(){
        Exception exception = assertThrows(InvalidFieldsException.class, () ->{
            userService.changeTheUserDescription("", USERNAME);
        });
        assertEquals("Error, your fields is blank or empty, please try again",exception.getMessage());
    }

    @Test
    void changeTheUserDescriptionFailedWhenTheUsernameIsBlank(){

        Exception exception = assertThrows(InvalidFieldsException.class, () ->{
            userService.changeTheUserDescription("test", "");
        });
        assertEquals("Error, your fields is blank or empty, please try again",exception.getMessage());
    }

    @Test
    void findAllUsersSuccess(){
        var users = userService.findAllUsers().getBody();
        assertEquals(2, users.size());
        assertEquals(USERNAME,users.get(0).getUsername());
        assertEquals(USERNAME_REGISTER,users.get(1).getUsername());
    }

    @Test
    void findUserByUsernameSuccess(){
        var user = userService.findUserByUsername(USERNAME).getBody();
        assertEquals(USERNAME, user.getUsername());
        assertEquals(PASSWORD, user.getPassword());
    }

    @Test
    void findUserByUsernameFailedWhenUsernameNotFound(){
        Exception exception = assertThrows(UserNotFoundException.class, () ->{
            userService.findUserByUsername(USERNAME_REGISTER);
        });
        assertEquals("Username AnnyBrian not found, try again", exception.getMessage());
    }

    @Test
    void findUserByUsernameFailedWhenUsernameIsNull(){
        Exception exception = assertThrows(UserNotFoundException.class, () ->{
            userService.findUserByUsername(null);
        });
        assertEquals("Username null not found, try again", exception.getMessage());
    }

    @Test
    void findUserByUsernameFailedWhenUsernameIsBlankOrEmpty(){
        Exception exception = assertThrows(UserNotFoundException.class, () ->{
            userService.findUserByUsername("");
        });
        assertEquals("Username  not found, try again", exception.getMessage());
    }

    @Test
    void checkFieldsBlank(){
        RegisterDTO register = new RegisterDTO("test", "test","test","");

        Exception exception = assertThrows(InvalidFieldsException.class, () ->{
            userService.verifyFields(register.fullName(),register.lastName(),register.username(),register.password());
        });
        assertEquals("Error, your fields is blank or empty, please try again",exception.getMessage());
    }

    @Test
    void checkFieldsNull(){
        RegisterDTO register = new RegisterDTO("test", null,"test","test");

        Exception exception = assertThrows(InvalidFieldsException.class, () ->{
            userService.verifyFields(register.fullName(),register.lastName(),register.username(),register.password());
        });
        assertEquals("Error, your fields is null, please try again",exception.getMessage());
    }
}
