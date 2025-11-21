package com.ratingsystem.service.impl;

import com.ratingsystem.dto.request.CommentRequestDto;
import com.ratingsystem.dto.request.SellerRequestDto;
import com.ratingsystem.dto.response.CommentResponseDto;
import com.ratingsystem.dto.response.SellerResponseDto;
import com.ratingsystem.entity.Comment;
import com.ratingsystem.entity.User;
import com.ratingsystem.repository.CommentRepository;
import com.ratingsystem.repository.UserRepository;
import com.ratingsystem.service.in.RatingService;
import com.ratingsystem.service.in.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final SellerService sellerService;

    @Override
    public List<Comment> findBySellerId(Integer sellerId) {
        return commentRepository.findBySellerId(sellerId);
    }

    @Override
    public List<Comment> findByApprovedSellerId(Integer sellerId, boolean approved) {
        return commentRepository.findBySellerIdAndApproved(sellerId, approved);
    }

    @Override
    public List<Comment> findByAuthorId(Integer authorId) {
        return commentRepository.findByAuthorId(authorId);
    }

    @Override
    public int countBySellerId(Integer sellerId) {
        return commentRepository.countBySellerId(sellerId);
    }

    @Override
    public int countByApprovedSellerIdAnd(Integer sellerId, boolean approved) {
        return commentRepository.countBySellerIdAndApproved(sellerId, approved);
    }

    @Override
    public Double getAverageRatingForSeller(Integer sellerId) {
        return commentRepository.getAverageRatingForSeller(sellerId);
    }

    // DTO functions
    @Override
    public CommentResponseDto getCommentById(Integer commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));
        return mapToDto(comment);
    }

    @Override
    public CommentResponseDto createComment(CommentRequestDto dto, Integer authorId) {
        User author = null;
        if (authorId != null) {
            author = userRepository.findById(authorId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Author not found"));
        }

        // find or create seller
        User seller;
        if (dto.getSellerId() != null) {
            seller = userRepository.findById(dto.getSellerId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seller not found"));
        } else {
            if (dto.getSellerEmail() == null ||
                    dto.getSellerFirstName() == null ||
                    dto.getSellerLastName() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Seller info is required when creating a new seller");
            }

            SellerRequestDto sReq = new SellerRequestDto(
                    dto.getSellerFirstName(),
                    dto.getSellerLastName(),
                    dto.getSellerEmail()
            );
            SellerResponseDto sRes = sellerService.createSeller(sReq);

            seller = userRepository.findById(sRes.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Failed to retrieve created seller"));
        }

        Comment comment = Comment.builder()
                .message(dto.getMessage())
                .rating(dto.getRating())
                .author(author)
                .seller(seller)
                .approved(false)
                .build();

        comment = commentRepository.save(comment);
        return mapToDto(comment);
    }

    @Override
    public CommentResponseDto approveComment(Integer commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));

        comment.setApproved(true);
        comment = commentRepository.save(comment);
        return mapToDto(comment);
    }

    @Override
    public CommentResponseDto declineComment(Integer commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));

        comment.setApproved(false);
        comment = commentRepository.save(comment);
        return mapToDto(comment);
    }

    @Override
    public void deleteComment(Integer commentId, Integer userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));

        boolean isAuthor =
                comment.getAuthor() != null && comment.getAuthor().getId().equals(userId);
        boolean isSeller = comment.getSeller().getId().equals(userId);

        if (!isAuthor && !isSeller) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You can delete only your own comments or comments on your account");
        }

        commentRepository.delete(comment);
    }

    // helper
    private CommentResponseDto mapToDto(Comment comment) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .message(comment.getMessage())
                .rating(comment.getRating())
                .authorId(comment.getAuthor() != null ? comment.getAuthor().getId() : null)
                .authorName(comment.getAuthor() != null
                        ? comment.getAuthor().getFirstName() + " " + comment.getAuthor().getLastName()
                        : "Anonymous")
                .createdAt(comment.getCreatedAt())
                .approved(comment.isApproved())
                .build();
    }


}
