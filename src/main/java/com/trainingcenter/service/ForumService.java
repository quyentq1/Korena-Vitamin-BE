package com.trainingcenter.service;

import com.trainingcenter.entity.*;
import com.trainingcenter.repository.*;
import com.trainingcenter.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ForumService {

    private final ForumCategoryRepository categoryRepository;
    private final ForumPostRepository postRepository;
    private final ForumCommentRepository commentRepository;

    public List<ForumCategory> getAllCategories() {
        return categoryRepository.findAll();
    }

    public ForumCategory createCategory(ForumCategory category) {
        return categoryRepository.save(category);
    }

    public List<ForumPost> getPostsByCategory(Long categoryId) {
        return postRepository.findByCategoryIdOrderByCreatedAtDesc(categoryId);
    }

    public List<ForumPost> getAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    public ForumPost createPost(ForumPost post) {
        post.setCreatedAt(LocalDateTime.now());
        post.setViews(0L);
        post.setCommentsCount(0);
        return postRepository.save(post);
    }

    public ForumPost getPostById(Long id) {
        ForumPost post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        // Increment view
        post.setViews(post.getViews() + 1);
        return postRepository.save(post);
    }

    public ForumComment addComment(Long postId, ForumComment comment, User author) {
        ForumPost post = getPostById(postId);
        
        comment.setPost(post);
        comment.setAuthor(author);
        comment.setCreatedAt(LocalDateTime.now());
        
        ForumComment saved = commentRepository.save(comment);
        
        // Update comment count
        post.setCommentsCount(post.getCommentsCount() + 1);
        postRepository.save(post);
        
        return saved;
    }

    public List<ForumComment> getCommentsByPost(Long postId) {
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId);
    }
}
