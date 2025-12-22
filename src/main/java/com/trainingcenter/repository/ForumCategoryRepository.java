package com.trainingcenter.repository;

import com.trainingcenter.entity.ForumCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForumCategoryRepository extends JpaRepository<ForumCategory, Long> {
}
