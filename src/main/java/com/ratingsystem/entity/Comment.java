package com.ratingsystem.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "TEXT")
    private String message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    @Column(name = "anonymous_email")
    private String anonymousEmail;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User seller;

    private LocalDateTime createdAt;
    private boolean approved;

    @Min(1)
    @Max(10)
    private int rating;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}