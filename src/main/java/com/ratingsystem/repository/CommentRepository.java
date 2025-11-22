package com.ratingsystem.repository;

import com.ratingsystem.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository  extends JpaRepository<Comment, Integer>  {
    // All comments for seller
    List<Comment> findBySellerId(Integer sellerId);

    // Only approved comments
    List<Comment> findBySellerIdAndApproved(Integer sellerId, boolean approved);

    // Comments written by AUTHOR
    List<Comment> findByAuthorId(Integer authorId);

    // Count all comments for seller
    int countBySellerId(Integer sellerId);

    // Count only approved comments
    int countBySellerIdAndApproved(Integer sellerId, boolean approved);

    @Query("""
    SELECT AVG(c.rating)
    FROM Comment c
    WHERE c.seller.id = :sellerId
      AND c.approved = true
""")
    Double getAverageRatingForSeller(@Param("sellerId") Integer sellerId);
}
