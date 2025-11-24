package com.ratingsystem.unit;

import com.ratingsystem.dto.response.SellerResponseDto;
import com.ratingsystem.entity.User;
import com.ratingsystem.enums.Role;
import com.ratingsystem.repository.CommentRepository;
import com.ratingsystem.repository.UserRepository;
import com.ratingsystem.service.impl.SellerServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SellerServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private SellerServiceImpl sellerService;

    @Test
    void testGetTopSellersReturnsSortedByRating() {

        // mock data
        User s1 = User.builder()
                .id(21)
                .role(Role.SELLER)
                .approved(true)
                .firstName("Alex")
                .lastName("Addams")
                .email("addams@mail.com")
                .createdAt(LocalDateTime.now())
                .build();

        User s2 = User.builder()
                .id(24)
                .role(Role.SELLER)
                .approved(true)
                .firstName("Lionel")
                .lastName("Messi")
                .email("messi@mail.com")
                .createdAt(LocalDateTime.now())
                .build();

        User s3 = User.builder()
                .id(28)
                .role(Role.SELLER)
                .approved(true)
                .firstName("Anna")
                .lastName("Catherina")
                .email("catherina@mail.com")
                .createdAt(LocalDateTime.now())
                .build();

        List<User> users = List.of(s1, s2, s3);

        // when userRepository is called
        when(userRepository.findByRoleAndApprovedTrue(Role.SELLER))
                .thenReturn(users);

        // when rating is calculated
        when(commentRepository.getAverageRatingForSeller(21)).thenReturn(4.9);
        when(commentRepository.getAverageRatingForSeller(24)).thenReturn(4.7);
        when(commentRepository.getAverageRatingForSeller(28)).thenReturn(4.6);

        // when count is calculated
        when(commentRepository.countBySellerId(21)).thenReturn(12);
        when(commentRepository.countBySellerId(24)).thenReturn(8);
        when(commentRepository.countBySellerId(28)).thenReturn(5);

        // call real service method
        List<SellerResponseDto> result = sellerService.getTopSellers(3);

        // validation
        assertEquals(3, result.size());
        assertEquals(4.9, result.get(0).getAverageRating());
        assertEquals(4.7, result.get(1).getAverageRating());
        assertEquals(4.6, result.get(2).getAverageRating());

        assertEquals("Alex", result.get(0).getFirstName());
        assertEquals("Lionel", result.get(1).getFirstName());
        assertEquals("Anna", result.get(2).getFirstName());
    }

    //tested
    /*
    Sellers are fetched from the repository
    Average rating per seller is fetched
    Comment count per seller is fetched
    Sorting occurs in descending order by rating
    Returned DTO contains correct fields
    userRepository returns list of sellers
    commentRepository returns fake ratings
    sellerService sorts sellers by rating
     */
}
