package org.example.profilecase5.Repository;


import org.example.profilecase5.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    User findByEmail(String email);
    @Query("SELECT u FROM User u WHERE u.role.id = 3")
    List<User> findAllOwners();
    Optional<User> findByUserId(int userId);
}
