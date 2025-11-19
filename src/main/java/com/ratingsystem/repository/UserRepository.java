package com.ratingsystem.repository;

import com.ratingsystem.Enum.Role;
import com.ratingsystem.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    // optional 'cause user may not exist and to avoid nulls
    Optional<User> findByEmail(String email);

    //Admin lists pending sellers
    List<User> findByRoleAndApprovedTrue(Role role);

    //Show only approved sellers
    List<User> findByRoleAndApprovedFalse(Role role);

    //Query for filtering sellers by game title and rating range
    @Query("""
SELECT u FROM User u
WHERE u.role = 'SELLER'
  AND u.approved = true
  AND EXISTS (
      SELECT g FROM GameObject g
      WHERE g.user.id = u.id
        AND g.title = :gameTitle
  )
  AND (
      SELECT AVG(c.rating) FROM Comment c
      WHERE c.seller.id = u.id
        AND c.approved = true
  ) BETWEEN :minRating AND :maxRating
ORDER BY (
      SELECT AVG(c.rating) FROM Comment c
      WHERE c.seller.id = u.id
        AND c.approved = true
) DESC
""")
    List<User> findSellersByGameAndRating(
            @Param("gameTitle") String gameTitle,
            @Param("minRating") Double minRating,
            @Param("maxRating") Double maxRating);


}
