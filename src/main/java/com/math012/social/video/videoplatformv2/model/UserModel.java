package com.math012.social.video.videoplatformv2.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
public class UserModel implements UserDetails {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JsonIgnore
    @Column(nullable = false)
    private String fullName;

    @JsonIgnore
    @Column(nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String username;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Column(nullable = true)
    private String description;

    @Column(nullable = true)
    private String profilePhoto;


    @OneToMany(mappedBy = "user")
    private List<VideoModel> videos = new ArrayList<>();

    public UserModel(){}

    public UserModel(String fullName, String lastName, String username, String password) {
        this.fullName = fullName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
    }

    public UserModel(String description, String profilePhoto) {
        this.description = description;
        this.profilePhoto = profilePhoto;
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

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public List<VideoModel> getVideos() {
        return videos;
    }

    public void setVideos(List<VideoModel> videos) {
        this.videos = videos;
    }
}