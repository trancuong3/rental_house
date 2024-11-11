package org.example.profilecase5.Repository;

import org.example.profilecase5.Model.PasswordHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PasswordHistoryRepository extends JpaRepository<PasswordHistory, Integer> {
    List<PasswordHistory> findByUserId(Long userId);
}
