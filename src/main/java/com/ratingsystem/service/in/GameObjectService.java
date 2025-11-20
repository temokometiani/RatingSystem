package com.ratingsystem.service.in;

import com.ratingsystem.dto.request.GameObjectRequestDto;
import com.ratingsystem.dto.response.GameObjectResponseDto;
import com.ratingsystem.entity.GameObject;
import com.ratingsystem.entity.User;

import java.util.List;

public interface GameObjectService {

    List<GameObject> findByUserId(Integer userId);
    List<GameObject> findByTitleContainingIgnoreCase(String title);
    void delete(Integer id, Integer userId);
    List<GameObjectResponseDto> getAll();
    GameObjectResponseDto create(GameObjectRequestDto dto, Integer userId);
    GameObjectResponseDto update(Integer id, GameObjectRequestDto dto, Integer userId);
    List<GameObjectResponseDto> searchByTitle(String title);
    List<GameObjectResponseDto> getBySeller(Integer sellerId);
}
