package com.math012.social.video.videoplatformv2.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;


import java.util.UUID;

@Entity
public class CommentModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Size(min = 1, max = 400, message = "Minumum is 1 and maximum is 400")
    private String text;

    @ManyToOne
    @JoinColumn(name = "uuid_user")
    private UserModel user;

    @ManyToOne
    @JoinColumn(name = "uuid_video")
    private VideoModel video;

    public CommentModel(){}

    public CommentModel(String text, UserModel user, VideoModel video) {
        this.text = text;
        this.user = user;
        this.video = video;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public VideoModel getVideo() {
        return video;
    }

    public void setVideo(VideoModel video) {
        this.video = video;
    }
}
