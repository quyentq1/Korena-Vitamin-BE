package com.trainingcenter.repository;

import com.trainingcenter.entity.ForumPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ForumPostRepository extends JpaRepository<ForumPost, Long> {
    List<ForumPost> findByCategoryIdOrderByCreatedAtDesc(Long categoryId);
    List<ForumPost> findAllByOrderByCreatedAtDesc();
}
