package com.math012.social.video.videoplatformv2.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public class UserModel {

    @Id
    private UUID id;

    @Size(min = 2, message = "full name is blank")
    private String fullName;

    @Size(min = 2, message = "last name is blank")
    private String lastName;

    @Size(min = 2, message = "username is blank")
    private String username;

    @Size(min = 8, max = 20, message = "your password needs a minimum of 8 characters and a maximum of 20")
    private String password;

    @OneToMany(mappedBy = "user")
    private List<VideoModel> videos = new ArrayList<>();

    public UserModel(){}

    public UserModel(UUID id, String fullName, String lastName, String username, String password) {
        this.id = id;
        this.fullName = fullName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<VideoModel> getVideos() {
        return videos;
    }

    public void setVideos(List<VideoModel> videos) {
        this.videos = videos;
    }
}