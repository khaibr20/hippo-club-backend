package com.khalid.hippoClub.Controller;

import com.khalid.hippoClub.Exception.ResponseException;
import com.khalid.hippoClub.Service.PostService;
import com.khalid.hippoClub.dto.PostReq;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/post")
@CrossOrigin
public class PostController {

    private final PostService postService;

    @GetMapping("/allPost")
    public ResponseEntity<?> getAllPosts() {
        return new ResponseEntity<>(postService.findAllPosts(), HttpStatus.OK);
    }

    @PostMapping("/{id}/upload")
    public ResponseEntity<?> createPost(@RequestBody PostReq req, @PathVariable("id") Long userId) {

        postService.addPost(req, userId);
        return new ResponseEntity<>(ResponseException.jsonResponse("success", "Image added successfully"), HttpStatus.CREATED);
    }

    @GetMapping("/userPosts/{id}")
    public ResponseEntity<?> getUserPost(@PathVariable("id") Long userId) {
        return new ResponseEntity<>(postService.findAllPostByUser(userId), HttpStatus.OK);
    }

    @GetMapping("/getPost/{id}")
    public ResponseEntity<?> getPost(@PathVariable("id") Long postId) {
        System.out.println(postId);
        return new ResponseEntity<>(postService.findPostById(postId), HttpStatus.OK);
    }

    @PutMapping("/editPost/{id}")
    public ResponseEntity<?> editPost(@RequestBody PostReq req, @PathVariable("id") Long postId) {

        if (postId == null)
            return new ResponseEntity<>(ResponseException.jsonResponse("error", "Post id " + postId + " not found"), HttpStatus.BAD_REQUEST);

        postService.updatePost(req, postId);
        return new ResponseEntity<>(ResponseException.jsonResponse("success", "Post updated successfully"), HttpStatus.CREATED);
    }

    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable("postId") Long postId) {

        if (postId == null)
            return new ResponseEntity<>(ResponseException.jsonResponse("error", "Post id " + postId + " not found"), HttpStatus.BAD_REQUEST);

        postService.deletePost(postId);
        return new ResponseEntity<>(ResponseException.jsonResponse("success", "Post deleted successfully"), HttpStatus.CREATED);
    }
}