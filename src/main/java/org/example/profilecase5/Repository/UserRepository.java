package org.example.profilecase5.Repository;

import org.example.profilecase5.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    // ===== Tìm kiếm cơ bản =====
    Optional<User> findByUsername(String username);
    Optional<User> findByUserId(int userId);
    User findByEmail(String email);

    // ===== Kiểm tra trùng =====
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);

    // ===== Lấy danh sách Owner =====
    @Query("SELECT u FROM User u WHERE u.role.roleId = 3")
    List<User> findAllOwners();

    // ===== Lấy Owner theo roleId và trạng thái =====
    List<User> findByRoleRoleIdAndStatus(int roleId, String status);
}
