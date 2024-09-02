package com.math012.social.video.videoplatformv2.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.UUID;

@Entity
public class VideoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Size(min = 4)
    private String title;

    @Size(min = 1)
    private String description;


    private String url;

    private Instant date;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "uuid_user")
    private UserModel user;

    public VideoModel(){}

    public VideoModel(String title, String description, String url, UserModel user, Instant date) {
        this.title = title;
        this.description = description;
        this.url = url;
        this.user = user;
        this.date = date;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }
}
