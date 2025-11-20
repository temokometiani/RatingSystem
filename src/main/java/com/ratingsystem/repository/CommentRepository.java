package com.ratingsystem.repository;

import com.ratingsystem.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository  extends JpaRepository<Comment, Integer>  {

    //Get all comments os seller
    List<Comment> findBySellerId(Integer sellerId);

    //get only approved comments of seller
    List<Comment> findByApprovedSellerId(Integer sellerId, boolean approved);


    List<Comment> findByAuthorId(Integer authorId);

    //count all comments
    int countBySellerId(Integer sellerId);

    //count approved comments
    int countByApprovedSellerId(Integer sellerId, boolean approved);

    //average rating of seller
    @Query("""
        SELECT AVG(c.rating)
        FROM Comment c
        WHERE c.seller.id = :sellerId
        AND c.approved = true
    """)
    Double getAverageRatingForSeller(Integer sellerId);
}
