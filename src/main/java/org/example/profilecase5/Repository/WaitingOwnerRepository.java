package org.example.profilecase5.Repository;

import jakarta.transaction.Transactional;
import org.example.profilecase5.Model.User;
import org.example.profilecase5.Model.WaitingOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WaitingOwnerRepository extends JpaRepository<WaitingOwner, Integer> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    @Modifying
    @Transactional
    @Query("DELETE FROM WaitingOwner wo WHERE wo.userId = :userId")
    void deleteByUserId(@Param("userId") int userId);

}
