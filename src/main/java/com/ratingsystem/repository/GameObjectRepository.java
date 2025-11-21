package com.ratingsystem.repository;
import com.ratingsystem.entity.GameObject;
import com.ratingsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface GameObjectRepository extends JpaRepository<GameObject, Integer>{
    // All game objects by seller ID
    List<GameObject> findByUserId(Integer userId);

    // Search by title ignore case
    List<GameObject> findByTitleContainingIgnoreCase(String title);

    // All objects by User entity
    List<GameObject> findByUser(User user);
}
