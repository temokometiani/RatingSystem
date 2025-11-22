package com.ratingsystem.controller;

import com.ratingsystem.dto.request.GameObjectRequestDto;
import com.ratingsystem.dto.response.GameObjectResponseDto;
import com.ratingsystem.entity.User;
import com.ratingsystem.service.in.GameObjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/objects")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Game Objects", description = "Add, edit, delete and browse game objects")
public class GameObjectController {

    private final GameObjectService gameObjectService;

    //create GameObject
    @PostMapping
    @Operation(summary = "Create game object (SELLER only)")
    public ResponseEntity<GameObjectResponseDto> create(
            @RequestBody GameObjectRequestDto dto,
            @AuthenticationPrincipal User user) {

        log.debug("User {} creating GameObject: {}", user.getId(), dto);
        GameObjectResponseDto response =
                gameObjectService.create(dto, user.getId());

        log.info("GameObject created, id={}", response.getId());
        return ResponseEntity.ok(response);
    }


    // delete gameobject
    @DeleteMapping("/{objectId}")
    @Operation(summary = "Delete game object by ID (SELLER only)")
    public ResponseEntity<Void> delete(
            @PathVariable Integer objectId,
            @AuthenticationPrincipal User user) {

        log.debug("User {} deleting GameObject {}", user.getId(), objectId);

        gameObjectService.delete(objectId, user.getId());

        log.info("GameObject {} deleted", objectId);
        return ResponseEntity.noContent().build();
    }

    // update game object
    @PutMapping("/{objectId}")
    @Operation(summary = "Update game object by ID (SELLER only)")
    public ResponseEntity<GameObjectResponseDto> update(
            @PathVariable Integer objectId,
            @RequestBody GameObjectRequestDto dto,
            @AuthenticationPrincipal User user) {

        log.debug("User {} updating GameObject id={}, dto={}",
                user.getId(), objectId, dto);

        GameObjectResponseDto response =
                gameObjectService.update(objectId, dto, user.getId());

        log.info("GameObject {} updated", objectId);
        return ResponseEntity.ok(response);
    }


    // get all gameobjects
    @GetMapping
    @Operation(summary = "Get all game objects (public)")
    public ResponseEntity<List<GameObjectResponseDto>> getAll() {

        log.debug("Fetching all game objects");

        List<GameObjectResponseDto> objects = gameObjectService.getAll();

        log.info("Fetched {} objects", objects.size());
        return ResponseEntity.ok(objects);
    }

    // search game object bt title
    @GetMapping("/search")
    @Operation(summary = "Search game objects by title (public)")
    public ResponseEntity<List<GameObjectResponseDto>> search(
            @RequestParam String title) {

        log.debug("Searching objects by title '{}'", title);

        List<GameObjectResponseDto> response =
                gameObjectService.searchByTitle(title);

        return ResponseEntity.ok(response);
    }

    // get seller's all game objects
    @GetMapping("/seller/{sellerId}")
    @Operation(summary = "Get objects owned by a seller (public)")
    public ResponseEntity<List<GameObjectResponseDto>> getBySeller(
            @PathVariable Integer sellerId) {

        log.debug("Fetching objects for seller {}", sellerId);

        List<GameObjectResponseDto> response = gameObjectService.getBySeller(sellerId);

        return ResponseEntity.ok(response);
    }

}
