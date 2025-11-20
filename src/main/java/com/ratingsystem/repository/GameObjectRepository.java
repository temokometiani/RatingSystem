package com.ratingsystem.repository;
import com.ratingsystem.entity.GameObject;
import com.ratingsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface GameObjectRepository extends JpaRepository<GameObject, Integer>{

    //Get all game objects of seller
    List<GameObject> findByUserId(Integer userId);

    //Search by title(case insensivite)
    List<GameObject> findByTitleContainingIgnoreCase(String title);

    //Get all game objects of seller
    List<GameObject> findByUser (User user);

    List<GameObject> findByUserID(Integer userId);
}
