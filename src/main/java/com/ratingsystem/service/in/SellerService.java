package com.ratingsystem.service.in;

import com.ratingsystem.dto.request.SellerRequestDto;
import com.ratingsystem.dto.response.SellerResponseDto;

import java.util.List;

public interface SellerService {

    SellerResponseDto createSeller(SellerRequestDto dto);
    SellerResponseDto approveSeller(Integer sellerId);
    List<SellerResponseDto> getAllSellers(boolean approvedOnly);
    Double calculateSellerRating(Integer sellerId);
    List<SellerResponseDto> getTopSellers(int limit);
    List<SellerResponseDto> filterSellersByGameAndRating(String gameTitle, Double minRating, Double maxRating);
}
