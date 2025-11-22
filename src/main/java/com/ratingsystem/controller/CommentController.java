package com.ratingsystem.controller;

import com.ratingsystem.dto.request.CommentRequestDto;
import com.ratingsystem.dto.response.CommentResponseDto;
import com.ratingsystem.entity.Comment;
import com.ratingsystem.entity.User;
import com.ratingsystem.service.in.RatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Comments", description = "Create, view, update and delete comments for sellers")
public class CommentController {

    private final RatingService ratingService;

    //create comment
    @PostMapping
    public ResponseEntity<CommentResponseDto> createComment(
            @RequestBody CommentRequestDto request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {

        Integer authorId = null;

        if (userDetails instanceof User) {
            User user = (User) userDetails;
            authorId = user.getId();
        }

        CommentResponseDto response = ratingService.createComment(request, authorId);
        return ResponseEntity.ok(response);
    }

    // get comment by id
    @GetMapping("/{commentId}")
    @Operation(summary = "Get comment by ID")
    public ResponseEntity<CommentResponseDto> getCommentById(@PathVariable Integer commentId) {

        log.debug("Fetching comment {}", commentId);

        CommentResponseDto response = ratingService.getCommentById(commentId);

        log.info("Fetched comment {}", commentId);
        return ResponseEntity.ok(response);
    }

    // get all comments of seller
    @GetMapping("/{sellerId}")
    public ResponseEntity<List<CommentResponseDto>> getComments(
            @PathVariable Integer sellerId,
            @RequestParam(defaultValue = "false") boolean approvedOnly
    ) {

        List<Comment> comments = ratingService.findByApprovedSellerId(sellerId, approvedOnly);

        // Cocvert manually
        List<CommentResponseDto> dtoList = comments.stream()
                .map(ratingService::mapToDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);
    }

    // delete comment
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Integer commentId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {

        if (!(userDetails instanceof User)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login required");
        }

        User user = (User) userDetails;
        ratingService.deleteComment(commentId, user.getId());

        return ResponseEntity.noContent().build();
    }
}
