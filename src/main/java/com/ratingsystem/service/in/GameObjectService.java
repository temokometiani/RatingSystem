package com.ratingsystem.service.in;

import com.ratingsystem.entity.GameObject;
import com.ratingsystem.entity.User;

import java.util.List;

public interface GameObjectService {

    List<GameObject> findByUserID(Integer userId);
    List<GameObject> findByTitleContainingIgnoreCase(String title);
    void delete(Integer id, Integer userId);

}
