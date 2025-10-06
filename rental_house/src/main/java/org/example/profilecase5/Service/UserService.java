package org.example.profilecase5.Service;

import org.example.profilecase5.Exception.User.*;
import org.example.profilecase5.Model.Role;
import org.example.profilecase5.Model.User;
import org.example.profilecase5.Repository.RoleRepository;
import org.example.profilecase5.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    // ==================== CÁC HÀM CƠ BẢN ====================
    public boolean isPhoneExist(String phone) {
        return userRepository.existsByPhone(phone);
    }

    public boolean isUsernameExist(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean isEmailExist(String email) {
        return userRepository.existsByEmail(email);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(int id) {
        return userRepository.findById(id).orElse(null);
    }

    public void saveUser(User user) {
        if (user.getAvatar() == null || user.getAvatar().isEmpty()) {
            user.setAvatar("/images/img_2.png");
        }
        userRepository.save(user);
    }

    public void updateUser(User user) {
        userRepository.save(user);
    }

    public void deleteUserById(int id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User không tồn tại với ID = " + id);
        }
        userRepository.deleteById(id);
    }

    // ==================== OWNER ====================
    public List<User> getAllOwners() {
        return userRepository.findAllOwners();
    }

    public List<User> getWaitingOwners() {
        return userRepository.findByRoleRoleIdAndStatus(3, "Pending");
    }

    public void approveOwner(int id) {
        User owner = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Owner không tồn tại"));
        owner.setStatus(User.Status.Active);
        userRepository.save(owner);
    }

    public void registerOwner(User owner) {
        if (isUsernameExist(owner.getUsername())) {
            throw new UsernameAlreadyExistsException("Tên đăng nhập đã tồn tại.");
        }
        if (isEmailExist(owner.getEmail())) {
            throw new EmailAlreadyExistsException("Email đã tồn tại.");
        }
        if (isPhoneExist(owner.getPhone())) {
            throw new PhoneAlreadyExistsException("Số điện thoại đã tồn tại.");
        }

        if (!owner.getPassword().equals(owner.getConfirmPassword())) {
            throw new PasswordValidationException("Mật khẩu xác nhận không khớp.");
        }

        if (owner.getPassword().length() < 6 || owner.getPassword().length() > 32) {
            throw new PasswordValidationException("Mật khẩu phải có độ dài từ 6 đến 32 ký tự.");
        }

        owner.setPassword(passwordEncoder.encode(owner.getPassword()));
        owner.setConfirmPassword(owner.getPassword());
        owner.setStatus(User.Status.Active);
        owner.setCreatedAt(Timestamp.from(Instant.now()));
        owner.setUpdatedAt(Timestamp.from(Instant.now()));

        Role ownerRole = roleRepository.findById(3)
                .orElseThrow(() -> new RuntimeException("Role OWNER không tồn tại."));
        owner.setRole(ownerRole);

        userRepository.save(owner);
    }

    // ==================== USER ====================
    public void registerUser(User user) {
        if (isUsernameExist(user.getUsername())) {
            throw new UsernameAlreadyExistsException("Tên đăng nhập đã tồn tại.");
        }
        if (isEmailExist(user.getEmail())) {
            throw new EmailAlreadyExistsException("Email đã tồn tại.");
        }
        if (!user.getPassword().equals(user.getConfirmPassword())) {
            throw new PasswordValidationException("Mật khẩu xác nhận không khớp.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setConfirmPassword(user.getPassword());
        user.setCreatedAt(Timestamp.from(Instant.now()));
        user.setUpdatedAt(Timestamp.from(Instant.now()));

        Role userRole = roleRepository.findByRoleName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Role USER không tồn tại."));
        user.setRole(userRole);
        user.setStatus(User.Status.Active);

        userRepository.save(user);
    }

    // ==================== TIỆN ÍCH ====================
    public void toggleUserStatus(int userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            if (user.getStatus() == User.Status.Active) {
                user.setStatus(User.Status.Locked);
            } else {
                user.setStatus(User.Status.Active);
            }
            userRepository.save(user);
        }
    }

    public Page<User> getUsersWithPagination(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            return userRepository.findByUsername(auth.getName()).orElse(null);
        }
        return null;
    }
}
