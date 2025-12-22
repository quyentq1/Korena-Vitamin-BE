package com.trainingcenter.controller;

import com.trainingcenter.entity.*;
import com.trainingcenter.service.ForumService;
import com.trainingcenter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/forum")
public class ForumController {

    @Autowired
    private ForumService forumService;

    @Autowired
    private UserService userService;

    @GetMapping("/categories")
    public ResponseEntity<List<ForumCategory>> getAllCategories() {
        return ResponseEntity.ok(forumService.getAllCategories());
    }

    @PostMapping("/categories")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ForumCategory> createCategory(@RequestBody ForumCategory category) {
        return ResponseEntity.ok(forumService.createCategory(category));
    }

    @GetMapping("/posts")
    public ResponseEntity<List<ForumPost>> getAllPosts(@RequestParam(required = false) Long categoryId) {
        if (categoryId != null) {
            return ResponseEntity.ok(forumService.getPostsByCategory(categoryId));
        }
        return ResponseEntity.ok(forumService.getAllPosts());
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<ForumPost> getPostById(@PathVariable Long id) {
        return ResponseEntity.ok(forumService.getPostById(id));
    }

    @PostMapping("/posts")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ForumPost> createPost(@RequestBody ForumPost post, Authentication authentication) {
        User author = userService.getUserByUsername(authentication.getName());
        post.setAuthor(author);
        return ResponseEntity.ok(forumService.createPost(post));
    }

    @GetMapping("/posts/{id}/comments")
    public ResponseEntity<List<ForumComment>> getComments(@PathVariable Long id) {
        return ResponseEntity.ok(forumService.getCommentsByPost(id));
    }

    @PostMapping("/posts/{id}/comments")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ForumComment> addComment(
            @PathVariable Long id,
            @RequestBody ForumComment comment,
            Authentication authentication) {
        User author = userService.getUserByUsername(authentication.getName());
        return ResponseEntity.ok(forumService.addComment(id, comment, author));
    }
}
