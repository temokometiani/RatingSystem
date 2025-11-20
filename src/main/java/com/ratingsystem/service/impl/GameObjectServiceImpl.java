package com.ratingsystem.service.impl;

import com.ratingsystem.dto.request.GameObjectRequestDto;
import com.ratingsystem.dto.response.GameObjectResponseDto;
import com.ratingsystem.entity.GameObject;
import com.ratingsystem.entity.User;
import com.ratingsystem.enums.Role;
import com.ratingsystem.repository.GameObjectRepository;
import com.ratingsystem.repository.UserRepository;
import com.ratingsystem.service.in.GameObjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameObjectServiceImpl implements GameObjectService{

    private final GameObjectRepository gameObjectRepository;
    private final UserRepository userRepository;

    @Override
    public List<GameObject> findByUserId(Integer userId){
        return gameObjectRepository.findByUserId(userId);
    }

    @Override
    public void delete(Integer id, Integer userId) {
        GameObject gameObject = gameObjectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Game object not found"));

        if (!gameObject.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can delete only your own objects");
        }

        gameObjectRepository.delete(gameObject);
    }

    @Override
    public List<GameObject> findByTitleContainingIgnoreCase(String title) {
        return gameObjectRepository.findByTitleContainingIgnoreCase(title);
    }


    // DTO based functions

    @Override
    public GameObjectResponseDto update(Integer id, GameObjectRequestDto dto, Integer userId) {
        GameObject gameObject = gameObjectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Game object not found"));

        if (!gameObject.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can edit only your own objects");
        }

        gameObject.setTitle(dto.getTitle());
        gameObject.setText(dto.getText());

        gameObject = gameObjectRepository.save(gameObject);
        return mapToDto(gameObject);
    }

    @Override
    public GameObjectResponseDto create(GameObjectRequestDto dto, Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (user.getRole() != Role.SELLER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only sellers can create game objects");
        }

        GameObject gameObject = GameObject.builder()
                .title(dto.getTitle())
                .text(dto.getText())
                .user(user)
                .build();

        gameObject = gameObjectRepository.save(gameObject);
        return mapToDto(gameObject);
    }

    @Override
    public List<GameObjectResponseDto> getBySeller(Integer sellerId) {
        return gameObjectRepository.findByUserId(sellerId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<GameObjectResponseDto> searchByTitle(String title) {
        return gameObjectRepository.findByTitleContainingIgnoreCase(title).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<GameObjectResponseDto> getAll() {
        return gameObjectRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    private GameObjectResponseDto mapToDto(GameObject gameObject) {
        return GameObjectResponseDto.builder()
                .id(gameObject.getId())
                .title(gameObject.getTitle())
                .text(gameObject.getText())
                .userId(gameObject.getUser().getId())
                .userName(gameObject.getUser().getFirstName() + " " + gameObject.getUser().getLastName())
                .createdAt(gameObject.getCreatedAt())
                .updatedAt(gameObject.getUpdatedAt())
                .build();
    }

}
