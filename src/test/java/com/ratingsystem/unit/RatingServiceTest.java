package com.ratingsystem.unit;

import com.ratingsystem.dto.request.CommentRequestDto;
import com.ratingsystem.dto.response.CommentResponseDto;
import com.ratingsystem.entity.Comment;
import com.ratingsystem.entity.User;
import com.ratingsystem.repository.CommentRepository;
import com.ratingsystem.repository.UserRepository;
import com.ratingsystem.service.impl.RatingServiceImpl;
import com.ratingsystem.service.in.SellerService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class RatingServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private SellerService sellerService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RatingServiceImpl ratingService;

    public RatingServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateComment_success() {

        // Given
        CommentRequestDto request = new CommentRequestDto();
        request.setMessage("Great seller!");
        request.setRating(5);
        request.setSellerId(22);

        User seller = new User();
        seller.setId(22);
        seller.setFirstName("John");
        seller.setLastName("Wick");

        User author = new User();
        author.setId(25);

        when(userRepository.findById(22)).thenReturn(java.util.Optional.of(seller));
        when(userRepository.findById(25)).thenReturn(java.util.Optional.of(author));

        Comment saved = new Comment();
        saved.setId(27);
        saved.setMessage("Great seller!");
        saved.setRating(5);
        saved.setAuthor(author);
        saved.setSeller(seller);
        saved.setApproved(false);

        when(commentRepository.save(any(Comment.class))).thenReturn(saved);

        //  When
        CommentResponseDto response = ratingService.createComment(request, 25);

        // Then
        assertNotNull(response);
        assertEquals(27, response.getId());
        assertEquals("Great seller!", response.getMessage());
        assertEquals(5, response.getRating());
        assertEquals(25, response.getAuthorId());
        assertFalse(response.isApproved());
    }

    //tested
    /*
     Returned response is not null
     Returned id matches saved entity
     Message matches input
     Rating matches input
     Author ID matches input
     approved defaults to false
     */
}
