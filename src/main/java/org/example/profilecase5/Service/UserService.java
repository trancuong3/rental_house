package org.example.profilecase5.Service;
import org.example.profilecase5.Model.RentalHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;


import org.example.profilecase5.Exception.User.EmailAlreadyExistsException;
import org.example.profilecase5.Exception.User.PasswordValidationException;
import org.example.profilecase5.Exception.User.UsernameAlreadyExistsException;
import org.example.profilecase5.Model.Role;
import org.example.profilecase5.Model.User;
import org.example.profilecase5.Repository.RoleRepository;
import org.example.profilecase5.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;
    public User getUserByUsername(String username) {
        // Sử dụng orElse(null) để trả về null nếu không tìm thấy người dùng
        return userRepository.findByUsername(username).orElse(null);
    }

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public User getUserById(int userId) {
        return userRepository.findById(userId).orElse(null);
    }
    public void updateUser(User user) {
        userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public boolean validateUserAndRole(String username, String password, String selectedRole) {
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Kiểm tra xem mật khẩu đã được mã hóa chưa
            if (!isPasswordEncrypted(user.getPassword())) {
                // Nếu chưa mã hóa, mã hóa và cập nhật vào DB
                String encodedPassword = passwordEncoder.encode(user.getPassword());
                user.setPassword(encodedPassword);
                user.setConfirmPassword(encodedPassword);
                userRepository.save(user);
            }

            // Kiểm tra mật khẩu và vai trò
            boolean passwordMatches = passwordEncoder.matches(password, user.getPassword());
            boolean hasRole = user.getRole() != null && user.getRole().getRoleName().equalsIgnoreCase("ROLE_" + selectedRole);

            return passwordMatches && hasRole;
        }
        return false;
    }

    public boolean isUsernameExist(String username) {
        return userRepository.existsByUsername(username);
    }
    public boolean isEmailExist(String email) {
        return userRepository.existsByEmail(email);
    }
    private boolean isPasswordEncrypted(String password) {
        return password != null && password.startsWith("$2a$");
    }

    public void encryptAllPasswords() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            if(!isPasswordEncrypted(user.getPassword())) {
                String encodedPassword = passwordEncoder.encode(user.getPassword());
                user.setPassword(encodedPassword);
                user.setConfirmPassword(encodedPassword);
                userRepository.save(user);
            }
        }
    }
    public void saveUser(User user) {
        userRepository.save(user);
    }

    public Set<RentalHistory> getRentalHistoriesByUserId(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        return user.getRentalHistories();
    }

    public void toggleUserStatus(int userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            if (user.getStatus() == User.Status.ACTIVE) {
                user.setStatus(User.Status.LOCKED);
            } else {
                user.setStatus(User.Status.ACTIVE);
            }
            userRepository.save(user);
        }
    }
    public Page<User> getUsersWithPagination(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAll(pageable);
    }

    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }
    public boolean isPasswordCorrect(String currentPassword, String storedPassword) {
        return passwordEncoder.matches(currentPassword, storedPassword);
    }
    public void registerUser(User user) {
        // Kiểm tra tên người dùng
        if (isUsernameExist(user.getUsername())) {
            throw new UsernameAlreadyExistsException("Vui lòng sử dụng tên đăng nhập khác.");
        }

        // Kiểm tra email
        if (isEmailExist(user.getEmail())) {
            throw new EmailAlreadyExistsException("Vui lòng sử dụng email khác.");
        }

        // Kiểm tra mật khẩu xác nhận
        if (!user.getPassword().equals(user.getConfirmPassword())) {
            // Ném exception nếu mật khẩu không khớp
            throw new PasswordValidationException("Mật khẩu xác nhận không khớp");
        }

        // Kiểm tra mật khẩu thô có thỏa mãn độ dài không
        if (user.getPassword().length() < 6 || user.getPassword().length() > 32) {
            // Ném exception nếu mật khẩu không hợp lệ
            throw new PasswordValidationException("Mật khẩu phải có độ dài từ 6 đến 32 ký tự");
        }
        Timestamp currentTimestamp = Timestamp.from(Instant.now());
        user.setCreatedAt(currentTimestamp);
        user.setUpdatedAt(currentTimestamp);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setConfirmPassword(passwordEncoder.encode(user.getConfirmPassword()));
        Role userRole = roleRepository.findByRoleId(user.getRole().getRoleId())
                .orElseGet(() -> roleRepository.findByRoleName("ROLE_USER")
                        .orElseThrow(() -> new RuntimeException("Role not found")));
        user.setRole(userRole);
        userRepository.save(user);
        if(validateUserAndRole(user.getUsername(), user.getPassword(),  user.getRole().getRoleName())) {
            encryptAllPasswords();
        }
    }
    public void registerOwnerUser(User user) {
        user.setRole(roleRepository.findByRoleName("ROLE_OWNER").orElse(null));
        userRepository.save(user);
    }
    @Transactional(readOnly = true) // Đảm bảo không thay đổi dữ liệu khi truy vấn
    public User getCurrentUser() {
        // Lấy Authentication từ SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Kiểm tra nếu người dùng đã đăng nhập và đã được xác thực
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            return userRepository.findByUsername(username).orElse(null);
        }

        // Trả về null nếu người dùng không đăng nhập hoặc không xác thực
        return null;
    }
}
