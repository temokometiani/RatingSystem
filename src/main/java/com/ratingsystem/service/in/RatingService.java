package com.ratingsystem.service.in;

import com.ratingsystem.dto.request.CommentRequestDto;
import com.ratingsystem.dto.response.CommentResponseDto;
import com.ratingsystem.entity.Comment;

import java.util.List;

public interface RatingService {

    CommentResponseDto mapToDto(Comment comment);
    List<Comment> findBySellerId(Integer sellerId);
    List<Comment> findByApprovedSellerId(Integer sellerId, boolean approved);
    List<Comment> findByAuthorId(Integer authorId);
    int countBySellerId(Integer sellerId);
    int countByApprovedSellerIdAnd(Integer sellerId, boolean approved);
    Double getAverageRatingForSeller(Integer sellerId);
    CommentResponseDto getCommentById(Integer commentId);
    CommentResponseDto createComment(CommentRequestDto dto, Integer authorId);
    CommentResponseDto approveComment(Integer commentId);
    CommentResponseDto declineComment(Integer commentId);
    void deleteComment(Integer commentId, Integer userId);


}
