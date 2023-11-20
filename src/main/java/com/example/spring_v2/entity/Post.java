package com.example.spring_v2.entity;

import com.example.spring_v2.dto.PostRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="post")
@NoArgsConstructor
public class Post extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="title",nullable = false)
    private String title;

    @Column(name = "username",nullable = false)
    private String username;

    @Column(name = "contents",nullable = false)
    private String contents;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Post(PostRequestDto requestDto, String username) {
        this.title = requestDto.getTitle();
        this.contents = requestDto.getContent();
        this.username = username;
    }

    public void update(PostRequestDto requestDto) {
        this.title = requestDto.getTitle();
        this.contents = requestDto.getContent();
    }

    public void setUser(User user) {
        this.user = user;
        user.getPostList().add(this);
    }
}