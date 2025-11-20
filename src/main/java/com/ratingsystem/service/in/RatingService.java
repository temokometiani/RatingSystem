package com.ratingsystem.service.in;

import com.ratingsystem.entity.Comment;

import java.util.List;

public interface RatingService {

    List<Comment> findBySellerId(Integer sellerId);
    List<Comment> findByApprovedSellerId(Integer sellerId, boolean approved);
    List<Comment> findByAuthorId(Integer authorId);
    int countBySellerId(Integer sellerId);
    int countByApprovedSellerIdAnd(Integer sellerId, boolean approved);
    Double getAverageRatingForSeller(Integer sellerId);


}
