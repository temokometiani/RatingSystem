package com.ratingsystem.controller;

import com.ratingsystem.dto.request.SellerRequestDto;
import com.ratingsystem.dto.response.SellerResponseDto;
import com.ratingsystem.service.in.SellerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sellers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Sellers", description = "Seller profiles, ratings, filters and top sellers")
public class SellerController {

    private final SellerService sellerService;

    // create seller
    @PostMapping
    @Operation(summary = "Create new seller (ADMIN only)")
    public ResponseEntity<SellerResponseDto> createSeller(
            @RequestBody SellerRequestDto dto) {

        log.debug("Creating new seller: {}", dto);

        SellerResponseDto response = sellerService.createSeller(dto);

        log.info("Seller created, id={}", response.getId());
        return ResponseEntity.ok(response);
    }

    // get all sellers
    @GetMapping
    @Operation(summary = "Get all sellers (public). Optionally only approved sellers.")
    public ResponseEntity<List<SellerResponseDto>> getAllSellers(
            @RequestParam(name = "approvedOnly", defaultValue = "false") boolean approvedOnly) {

        log.debug("Fetching all sellers. approvedOnly={}", approvedOnly);

        List<SellerResponseDto> response = sellerService.getAllSellers(approvedOnly);

        log.info("Returned {} sellers", response.size());
        return ResponseEntity.ok(response);
    }

    // search sellers via rating range and title
    @GetMapping("/filter")
    @Operation(summary = "Filter sellers by game title and rating range (public)")
    public ResponseEntity<List<SellerResponseDto>> filterSellers(
            @RequestParam("gameTitle") String gameTitle,
            @RequestParam("minRating") Double minRating,
            @RequestParam("maxRating") Double maxRating) {

        log.debug("Filtering sellers: title='{}', min={}, max={}",
                gameTitle, minRating, maxRating);

        List<SellerResponseDto> response =
                sellerService.filterSellersByGameAndRating(gameTitle, minRating, maxRating);

        log.info("Returned {} filtered sellers", response.size());
        return ResponseEntity.ok(response);
    }

    //get seller rating
    @GetMapping("/{sellerId}/rating")
    @Operation(summary = "Get average rating of seller (public)")
    public ResponseEntity<Double> getSellerRating(@PathVariable("sellerId") Integer sellerId) {

        log.debug("Fetching rating for seller {}", sellerId);

        Double rating = sellerService.calculateSellerRating(sellerId);

        log.info("Seller {} rating = {}", sellerId, rating);
        return ResponseEntity.ok(rating);
    }

    // get top sellers (based on rating)
    @GetMapping("/top")
    @Operation(summary = "Get top sellers by rating (public)")
    public ResponseEntity<List<SellerResponseDto>> getTopSellers(
            @RequestParam(name = "limit",defaultValue = "3") int limit) {

        log.debug("Fetching top {} sellers", limit);

        List<SellerResponseDto> response = sellerService.getTopSellers(limit);

        log.info("Returned {} top sellers", response.size());
        return ResponseEntity.ok(response);
    }

}
