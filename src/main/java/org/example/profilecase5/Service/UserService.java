package org.example.profilecase5.Service;

import org.example.profilecase5.Model.RentalHistory;
import org.example.profilecase5.Exception.User.EmailAlreadyExistsException;
import org.example.profilecase5.Exception.User.PasswordValidationException;
import org.example.profilecase5.Exception.User.UsernameAlreadyExistsException;
import org.example.profilecase5.Model.Role;
import org.example.profilecase5.Model.User;
import org.example.profilecase5.Repository.RoleRepository;
import org.example.profilecase5.Repository.UserRepository;
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

    protected final UserRepository userRepository;
    protected final PasswordEncoder passwordEncoder;
    protected final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
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

            if (!isPasswordEncrypted(user.getPassword())) {
                String encodedPassword = passwordEncoder.encode(user.getPassword());
                user.setPassword(encodedPassword);
                user.setConfirmPassword(encodedPassword);
                userRepository.save(user);
            }

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
            if (!isPasswordEncrypted(user.getPassword())) {
                String encodedPassword = passwordEncoder.encode(user.getPassword());
                user.setPassword(encodedPassword);
                user.setConfirmPassword(encodedPassword);
                userRepository.save(user);
            }
        }
    }

    public void saveUser(User user) {
        if (user.getAvatar() == null || user.getAvatar().isEmpty()) {
            user.setAvatar("/images/img_2.png");
        }
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

    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean isPasswordCorrect(String currentPassword, String storedPassword) {
        return passwordEncoder.matches(currentPassword, storedPassword);
    }

    public void registerUser(User user) {
        if (isUsernameExist(user.getUsername())) {
            throw new UsernameAlreadyExistsException("Vui lòng sử dụng tên đăng nhập khác.");
        }

        if (isEmailExist(user.getEmail())) {
            throw new EmailAlreadyExistsException("Vui lòng sử dụng email khác.");
        }

        if (user.getConfirmPassword() == null || user.getConfirmPassword().isEmpty()) {
            throw new PasswordValidationException("Xác nhận mật khẩu không được để trống");
        }

        if (!user.getPassword().equals(user.getConfirmPassword())) {
            throw new PasswordValidationException("Mật khẩu xác nhận không khớp");
        }

        if (user.getPassword().length() < 6 || user.getPassword().length() > 32) {
            throw new PasswordValidationException("Mật khẩu phải có độ dài từ 6 đến 32 ký tự");
        }

        Timestamp currentTimestamp = Timestamp.from(Instant.now());
        user.setCreatedAt(currentTimestamp);
        user.setUpdatedAt(currentTimestamp);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setConfirmPassword(passwordEncoder.encode(user.getConfirmPassword()));

        Role userRole = roleRepository.findByRoleName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Role không tồn tại"));
        user.setRole(userRole);
        userRepository.save(user);
        encryptAllPasswords();
    }

    public void registerOwnerUser(User user) {
        if (isUsernameExist(user.getUsername())) {
            throw new UsernameAlreadyExistsException("Vui lòng sử dụng tên đăng nhập khác.");
        }

        if (isEmailExist(user.getEmail())) {
            throw new EmailAlreadyExistsException("Vui lòng sử dụng email khác.");
        }

        if (!user.getPassword().equals(user.getConfirmPassword())) {
            throw new PasswordValidationException("Mật khẩu xác nhận không khớp");
        }

        if (user.getPassword().length() < 6 || user.getPassword().length() > 32) {
            throw new PasswordValidationException("Mật khẩu phải có độ dài từ 6 đến 32 ký tự");
        }

        Timestamp currentTimestamp = Timestamp.from(Instant.now());
        user.setCreatedAt(currentTimestamp);
        user.setUpdatedAt(currentTimestamp);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setConfirmPassword(passwordEncoder.encode(user.getConfirmPassword()));

        Role userRole = roleRepository.findByRoleName("ROLE_OWNER")
                .orElseThrow(() -> new RuntimeException("Role không tồn tại"));
        user.setRole(userRole);
        userRepository.save(user);
        encryptAllPasswords();
    }

    public List<User> getAllOwners() {
        return userRepository.findAllOwners();
    }

    @Transactional(readOnly = true)
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return userRepository.findByUsername(authentication.getName()).orElse(null);
        }
        return null;
    }

    // ====================================================================
    // ======= PHƯƠNG THỨC BỔ SUNG CHO ADMINCONTROLLER =======
    // ====================================================================

    public void deleteUserById(int userId) {
        userRepository.deleteById(userId);
    }

    public List<User> getWaitingOwners() {
        // Giả định Owner có roleId = 3 và trạng thái chờ là "Waiting"
        // Phương thức này truy vấn bằng chuỗi "Waiting" trong CSDL
        return userRepository.findByRoleRoleIdAndStatus(3, "Waiting");
    }

    /**
     * Duyệt Owner (Chuyển trạng thái từ Waiting sang Active).
     * Đã sửa lỗi: Dùng so sánh chuỗi 'Waiting' vì User.Status không chứa 'Waiting'.
     */
    public void approveOwner(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Owner không tồn tại"));

        // Sửa lỗi biên dịch/logic: Kiểm tra trạng thái bằng cách so sánh chuỗi
        // (Đây là cách an toàn nhất khi enum thiếu giá trị)
        if (user.getRole().getRoleId() == 3 && "Waiting".equalsIgnoreCase(user.getStatus().name())) {
            user.setStatus(User.Status.Active);
            userRepository.save(user);
        } else {
            throw new RuntimeException("Người dùng không phải là Owner chờ duyệt (Waiting Owner).");
        }
    }
}