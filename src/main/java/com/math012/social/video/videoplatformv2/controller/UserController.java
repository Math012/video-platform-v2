package com.math012.social.video.videoplatformv2.controller;

import com.math012.social.video.videoplatformv2.DTO.RegisterDTO;
import com.math012.social.video.videoplatformv2.DTO.UserDTO;
import com.math012.social.video.videoplatformv2.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
public class UserController {
    @Autowired
    private UserService userService;


    @PostMapping("/v2/auth/login")
    public ResponseEntity<String> login(@RequestBody UserDTO userDTO){
        return userService.login(userDTO);
    }

    @PostMapping("/v2/auth/register")
    public ResponseEntity<String> register(@RequestBody RegisterDTO registerDTO){
        return userService.register(registerDTO);
    }
}
