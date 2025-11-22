package com.ratingsystem.controller;

import com.ratingsystem.dto.response.CommentResponseDto;
import com.ratingsystem.dto.response.SellerResponseDto;
import com.ratingsystem.service.in.RatingService;
import com.ratingsystem.service.in.SellerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin", description = "Endpoints for approving or declining sellers and comments")
public class AdminController {


    private final SellerService sellerService;
    private final RatingService commentService;

    // approve seller
    @PutMapping("/sellers/{sellerId}/approve")
    @Operation(summary = "Approve seller", description = "Marks seller as approved so they become visible in the system")
    public ResponseEntity<SellerResponseDto> approveSeller(@PathVariable("sellerId") Integer sellerId) {
        log.debug("Admin requested to approve seller with ID: {}", sellerId);

        SellerResponseDto response = sellerService.approveSeller(sellerId);

        log.info("Seller {} approved by admin", sellerId);
        return ResponseEntity.ok(response);
    }

    //  approve comment

    @PutMapping("/comments/{commentId}/approve")
    @Operation(summary = "Approve comment", description = "Marks comment as approved so it becomes visible to users")
    public ResponseEntity<CommentResponseDto> approveComment(@PathVariable("commentId") Integer commentId) {
        log.debug("Admin requested to approve comment with ID: {}", commentId);

        CommentResponseDto response = commentService.approveComment(commentId);

        log.info("Comment {} approved by admin", commentId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/comments/{commentId}/decline")
    @Operation(summary = "Decline comment", description = "Marks comment as declined so it does not appear to users")
    public ResponseEntity<CommentResponseDto> declineComment(@PathVariable("commentId") Integer commentId) {
        log.debug("Admin requested to decline comment with ID: {}", commentId);

        CommentResponseDto response = commentService.declineComment(commentId);

        log.info("Comment {} declined by admin", commentId);
        return ResponseEntity.ok(response);
    }

}
