package com.ratingsystem.service.in;

import com.ratingsystem.dto.request.AuthenticationRequestDto;
import com.ratingsystem.dto.request.RegisterRequestDto;
import com.ratingsystem.dto.response.AuthenticationResponseDto;
import com.ratingsystem.entity.User;

public interface UserService {
    User findByEmail(String email);
    User findById(Integer id);
}

