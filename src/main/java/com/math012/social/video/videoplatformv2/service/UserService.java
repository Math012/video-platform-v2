package com.math012.social.video.videoplatformv2.service;

import com.math012.social.video.videoplatformv2.DTO.RegisterDTO;
import com.math012.social.video.videoplatformv2.DTO.UserDTO;
import com.math012.social.video.videoplatformv2.config.security.TokenProvider;
import com.math012.social.video.videoplatformv2.exception.RegisterUserException;
import com.math012.social.video.videoplatformv2.exception.SecurityAuthenticationException;
import com.math012.social.video.videoplatformv2.exception.TokenProviderException;
import com.math012.social.video.videoplatformv2.exception.UserNotFoundException;
import com.math012.social.video.videoplatformv2.model.UserModel;
import com.math012.social.video.videoplatformv2.repository.UserRepository;

import jakarta.validation.Valid;
import jdk.swing.interop.SwingInterOpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Objects;

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
        return userRepository.findByUsername(username).orElseThrow();
    }

    public ResponseEntity<String> login(@RequestBody @Valid UserDTO userDTO){
        try {
            authenticationManager = context.getBean(AuthenticationManager.class);
            var authenticate = new UsernamePasswordAuthenticationToken(userDTO.username(),userDTO.password());
            var authenticated = this.authenticationManager.authenticate(authenticate);
            var token = tokenProvider.tokenProvider((UserModel) authenticated.getPrincipal());
            return ResponseEntity.ok(token);
        }catch (Exception e){
            throw new UserNotFoundException("Your credentials are not valid, try again");
        }

    }

    public ResponseEntity<String> register(@RequestBody @Valid RegisterDTO registerDTO){
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
}
