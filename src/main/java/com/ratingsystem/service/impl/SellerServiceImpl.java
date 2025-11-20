package com.ratingsystem.service.impl;

import com.ratingsystem.dto.request.SellerRequestDto;
import com.ratingsystem.dto.response.SellerResponseDto;
import com.ratingsystem.entity.User;
import com.ratingsystem.enums.Role;
import com.ratingsystem.repository.CommentRepository;
import com.ratingsystem.repository.UserRepository;
import com.ratingsystem.service.in.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SellerServiceImpl implements SellerService {

    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public SellerResponseDto createSeller(SellerRequestDto dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already in use");
        }

        String tempPassword = UUID.randomUUID().toString().substring(0, 8);

        User seller = User.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(tempPassword))
                .role(Role.SELLER)
                .approved(false)
                .emailConfirmed(false)
                .build();

        seller = userRepository.save(seller);
        return mapToDto(seller);
    }

    @Override
    public SellerResponseDto approveSeller(Integer sellerId) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seller not found"));

        if (seller.getRole() != Role.SELLER) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not a seller");
        }

        seller.setApproved(true);
        seller = userRepository.save(seller);

        return mapToDto(seller);
    }

    @Override
    public List<SellerResponseDto> getAllSellers(boolean approvedOnly) {
        List<User> sellers = approvedOnly
                ? userRepository.findByRoleAndApprovedTrue(Role.SELLER)
                : userRepository.findByRoleAndApprovedFalse(Role.SELLER);

        return sellers.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Double calculateSellerRating(Integer sellerId) {
        return commentRepository.getAverageRatingForSeller(sellerId);
    }

    @Override
    public List<SellerResponseDto> getTopSellers(int limit) {
        List<User> sellers = userRepository.findByRoleAndApprovedTrue(Role.SELLER);

        return sellers.stream()
                .map(this::mapToDtoWithStats)
                .sorted((s1, s2) -> Double.compare(s2.getAverageRating(), s1.getAverageRating()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public List<SellerResponseDto> filterSellersByGameAndRating(String gameTitle, Double minRating, Double maxRating) {
        var sellers = userRepository.findSellersByGameAndRating(gameTitle, minRating, maxRating);

        return sellers.stream()
                .map(this::mapToDtoWithStats)
                .collect(Collectors.toList());
    }

    // helper

    private SellerResponseDto mapToDto(User seller) {
        Double avg = commentRepository.getAverageRatingForSeller(seller.getId());
        int count = commentRepository.countBySellerId(seller.getId());

        return SellerResponseDto.builder()
                .id(seller.getId())
                .firstName(seller.getFirstName())
                .lastName(seller.getLastName())
                .email(seller.getEmail())
                .createdAt(seller.getCreatedAt())
                .approved(seller.isApproved())
                .averageRating(avg != null ? avg : 0.0)
                .commentCount(count)
                .build();
    }

    private SellerResponseDto mapToDtoWithStats(User seller) {
        return mapToDto(seller);
    }
}
