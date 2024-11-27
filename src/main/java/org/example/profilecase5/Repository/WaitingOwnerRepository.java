package org.example.profilecase5.Repository;

import org.example.profilecase5.Model.User;
import org.example.profilecase5.Model.WaitingOwner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WaitingOwnerRepository extends JpaRepository<WaitingOwner, Integer> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
