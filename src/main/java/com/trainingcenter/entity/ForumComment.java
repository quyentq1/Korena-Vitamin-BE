package com.trainingcenter.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "forum_comments")
public class ForumComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private ForumPost post;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    private LocalDateTime createdAt = LocalDateTime.now();
}
