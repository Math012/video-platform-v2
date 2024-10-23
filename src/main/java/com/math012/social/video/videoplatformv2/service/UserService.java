package com.math012.social.video.videoplatformv2.service;

import com.math012.social.video.videoplatformv2.DTO.RegisterDTO;
import com.math012.social.video.videoplatformv2.DTO.UserDTO;
import com.math012.social.video.videoplatformv2.config.security.TokenProvider;
import com.math012.social.video.videoplatformv2.exception.*;
import com.math012.social.video.videoplatformv2.model.UserModel;
import com.math012.social.video.videoplatformv2.model.VideoModel;
import com.math012.social.video.videoplatformv2.repository.UserRepository;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;


@Service
public class UserService implements UserDetailsService {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenProvider tokenProvider;

    private AuthenticationManager authenticationManager;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(()-> new UsernameNotFoundException("User not found with username: " + username));
    }

    public ResponseEntity<String> login(@RequestBody UserDTO userDTO){
        verifyFields(userDTO.username(),userDTO.password());
        try {
            authenticationManager = context.getBean(AuthenticationManager.class);
            UsernamePasswordAuthenticationToken authenticate = new UsernamePasswordAuthenticationToken(userDTO.username(),userDTO.password());
            Authentication authenticated = this.authenticationManager.authenticate(authenticate);
            var token = tokenProvider.tokenProvider((UserModel) authenticated.getPrincipal());
            return ResponseEntity.ok(token);
        }catch (Exception e){
            throw new UserNotFoundException("Your credentials are not valid, try again");
        }
    }

    public ResponseEntity<String> register(@RequestBody @Valid RegisterDTO registerDTO){
        verifyFields(registerDTO.fullName(), registerDTO.lastName(), registerDTO.username(), registerDTO.password());
        if (userRepository.findByUsername(registerDTO.username()).isPresent()) throw new SecurityAuthenticationException("This username: " + registerDTO.username() + " already registered");
        try {
            String encryptedPassword = new BCryptPasswordEncoder().encode(registerDTO.password());
            var newUser = new UserModel(registerDTO.fullName(),registerDTO.lastName(),registerDTO.username(),encryptedPassword);
            userRepository.save(newUser);
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(newUser.getId())
                    .toUri();
            return ResponseEntity.created(location).build();
        }catch (Exception e){
            throw new RegisterUserException("Your account has not been registered, please try again");
        }
    }


    public ResponseEntity<List<VideoModel>> findAllVideosByUsername(String username){
        if (userRepository.findByUsername(username).isEmpty()) throw new UserNotFoundException("The user's "+username+" videos could not be loaded, please try again");
        return ResponseEntity.ok(userRepository.findAllVideosByUsername(username));

    }

    public ResponseEntity<String> changeTheUserDescription(String description, String username){
        verifyFields(description,username);
        if (userRepository.findByUsername(username).isEmpty()) throw new UserNotFoundException("Username not found, try again");
        var user = userRepository.findByUsername(username).get();
        String descriptionFormated = description.substring(0, 1).toUpperCase()+description.substring(1);
        user.setDescription(descriptionFormated);
        userRepository.save(user);
        return ResponseEntity.ok("Description has been changed");
    }

    public ResponseEntity<List<UserModel>> findAllUsers(){
        return ResponseEntity.ok(userRepository.findAll());
    }

    public ResponseEntity<UserModel> findUserByUsername(String username){
        if (userRepository.findByUsername(username).isPresent()){
            return ResponseEntity.ok(userRepository.findByUsername(username).get());
        }else {
            throw new UserNotFoundException("Username "+username+ " not found, try again");
        }
    }

    public UserModel returnUserFromUsername(String username){
        if (userRepository.findByUsername(username).isPresent()){
            return userRepository.findByUsername(username).get();
        }else throw new UserNotFoundException("The username " + username+" not found, try again");
    }

    public void saveUser(UserModel user){
        if (user != null){
            userRepository.save(user);
        }else throw new VideoNotFoundException("The user is null, try again");
    }

    public void verifyFields(String... fields){
        for (String campos: fields){
            if (campos == null) throw new InvalidFieldsException("Error, your fields is null, please try again");
            if (campos.isBlank() || campos.isEmpty()) throw new InvalidFieldsException("Error, your fields is blank or empty, please try again");
        }
    }
}
