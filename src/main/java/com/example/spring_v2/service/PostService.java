package com.example.spring_v2.service;

import com.example.spring_v2.dto.DeleteReponseDto;
import com.example.spring_v2.dto.PostRequestDto;
import com.example.spring_v2.dto.PostResponseDto;
import com.example.spring_v2.entity.Post;
import com.example.spring_v2.entity.User;
import com.example.spring_v2.jwt.JwtUtil;
import com.example.spring_v2.repository.PostRepository;
import com.example.spring_v2.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    private final JwtUtil jwtUtil;

    @Transactional
    public PostResponseDto createPost(PostRequestDto requestDto, HttpServletRequest request) {

        String token = jwtUtil.resolveToken(request);
        Claims claims;

        if (token != null) {

            if (jwtUtil.validateToken(token)) {

                claims = jwtUtil.getUserInfoFromToken(token);
            }else {
                throw new IllegalArgumentException("Token Error");
            }


            User user = userRepository.findByUsername(claims.getSubject()).orElseThrow(
                    () -> new IllegalArgumentException("로그인 해주세요")
            );


            Post post = new Post(requestDto, user.getUsername());

            post.setUser(user);

            postRepository.save(post);
            return new PostResponseDto(post);
        }else {
            return null;
        }
    }

    @Transactional
    public List<PostResponseDto> getPostList(){
        List<Post> postList = postRepository.findAllByOrderByModifiedAtDesc();
        List<PostResponseDto> responseDto = new ArrayList<>();

        for (Post post : postList) {
            responseDto.add(new PostResponseDto(post));
        }
        return responseDto;
    }

    @Transactional
    public PostResponseDto getPost(Long id) {
        Post post = postRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("게시글이 존재하지 않습니다.")
        );
        return new PostResponseDto(post);
    }

    @Transactional
    public PostResponseDto update(Long Id, PostRequestDto requestDto, HttpServletRequest request) {
        String token = jwtUtil.resolveToken(request);
        Claims claims;

        if (token != null) {
            if (jwtUtil.validateToken(token)) {
                claims = jwtUtil.getUserInfoFromToken(token);
            }else {
                throw new IllegalArgumentException("Token Error");
            }

            User user = userRepository.findByUsername(claims.getSubject()).orElseThrow(
                    () -> new IllegalArgumentException("로그인 해주세요")
            );
            Post post = postRepository.findById(Id).orElseThrow(
                    () -> new IllegalArgumentException("해당 게시물이 존재하지 않습니다.")
            );

            if(!post.getUsername().equals(user.getUsername())){
                throw new IllegalArgumentException("해당 게시물에 권한이 존재하지 않습니다.");
            }

            post.update(requestDto);
            postRepository.save(post);
            return new PostResponseDto(post);
        }else {
            return null;
        }
    }

    @Transactional
    public DeleteReponseDto delete(Long Id, HttpServletRequest request) {
        String token = jwtUtil.resolveToken(request);
        Claims claims;
        if (token != null) {
            if (jwtUtil.validateToken(token)) {
                claims = jwtUtil.getUserInfoFromToken(token);
            }else {
                throw new IllegalArgumentException("Token Error");
            }
            User user = userRepository.findByUsername(claims.getSubject()).orElseThrow(
                    () -> new IllegalArgumentException("로그인 해주세요")
            );

            Post post = postRepository.findById(Id).orElseThrow(
                    () -> new IllegalArgumentException("해당 게시물이 존재하지 않습니다.")
            );

            if(!post.getUsername().equals(user.getUsername())){
                throw new IllegalArgumentException("해당 게시물에 권한이 존재하지 않습니다.");
            }

            postRepository.deleteById(Id);
            return new DeleteReponseDto("게시글 삭제 성공",200);
        }
        return null;
    }
}