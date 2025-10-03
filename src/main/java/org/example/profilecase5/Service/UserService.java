package org.example.profilecase5.Service;

import org.example.profilecase5.Exception.User.PhoneAlreadyExistsException;
import org.example.profilecase5.Model.RentalHistory;
import org.example.profilecase5.Exception.User.EmailAlreadyExistsException;
import org.example.profilecase5.Exception.User.PasswordValidationException;
import org.example.profilecase5.Exception.User.UsernameAlreadyExistsException;
import org.example.profilecase5.Model.Role;
import org.example.profilecase5.Model.User;
import org.example.profilecase5.Repository.RoleRepository;
import org.example.profilecase5.Repository.UserRepository;
import org.example.profilecase5.Repository.WaitingOwnerRepository;
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

    protected final UserRepository userRepository;
    protected final PasswordEncoder passwordEncoder;
    protected final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }
    public boolean isPhoneExist(String phone) {
        return userRepository.existsByPhone(phone); // <-- thêm phương thức này
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

            // Kiểm tra mật khẩu mã hóa
            if (!isPasswordEncrypted(user.getPassword())) {
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
    public void deleteUserById(int id) {
        userRepository.deleteById(id);
    }
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
    public Role getRoleById(int roleId) {
        return roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));
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
            // Gán giá trị mặc định cho avatar nếu không có avatar
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
        // Kiểm tra tên người dùng
        if (isUsernameExist(user.getUsername())) {
            throw new UsernameAlreadyExistsException("Vui lòng sử dụng tên đăng nhập khác.");
        }

        // Kiểm tra email
        if (isEmailExist(user.getEmail())) {
            throw new EmailAlreadyExistsException("Vui lòng sử dụng email khác.");
        }

        // Kiểm tra mật khẩu xác nhận
        if (user.getConfirmPassword() == null || user.getConfirmPassword().isEmpty()) {
            throw new PasswordValidationException("Xác nhận mật khẩu không được để trống");
        }

        if (!user.getPassword().equals(user.getConfirmPassword())) {
            throw new PasswordValidationException("Mật khẩu xác nhận không khớp");
        }

        // Kiểm tra độ dài mật khẩu
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
    // Trong UserService
    public void registerOwner(User owner) {
        // Kiểm tra username
        if (isUsernameExist(owner.getUsername())) {
            throw new UsernameAlreadyExistsException("Vui lòng sử dụng tên đăng nhập khác.");
        }

        // Kiểm tra email
        if (isEmailExist(owner.getEmail())) {
            throw new EmailAlreadyExistsException("Vui lòng sử dụng email khác.");
        }

        // Kiểm tra số điện thoại
        if (isPhoneExist(owner.getPhone())) {
            throw new PhoneAlreadyExistsException("Số điện thoại đã tồn tại.");
        }

        // Kiểm tra mật khẩu xác nhận
        if (owner.getConfirmPassword() == null || owner.getConfirmPassword().isEmpty()) {
            throw new PasswordValidationException("Xác nhận mật khẩu không được để trống");
        }

        if (!owner.getPassword().equals(owner.getConfirmPassword())) {
            throw new PasswordValidationException("Mật khẩu xác nhận không khớp");
        }

        // Kiểm tra độ dài mật khẩu
        if (owner.getPassword().length() < 6 || owner.getPassword().length() > 32) {
            throw new PasswordValidationException("Mật khẩu phải có độ dài từ 6 đến 32 ký tự");
        }

        Timestamp currentTimestamp = Timestamp.from(Instant.now());
        owner.setCreatedAt(currentTimestamp);
        owner.setUpdatedAt(currentTimestamp);

        // Encode mật khẩu
        owner.setPassword(passwordEncoder.encode(owner.getPassword()));
        owner.setConfirmPassword(passwordEncoder.encode(owner.getConfirmPassword()));

        // 🔹 Set role mặc định Owner (roleId = 3)
        Role ownerRole = roleRepository.findById(3)
                .orElseThrow(() -> new RuntimeException("Role Owner không tồn tại"));
        owner.setRole(ownerRole);

        owner.setStatus(User.Status.Active);

        userRepository.save(owner);

        encryptAllPasswords(); // nếu bạn vẫn muốn gọi
    }


    public void registerOwnerUser(User user) {
        if (isUsernameExist(user.getUsername())) {
            throw new UsernameAlreadyExistsException("Vui lòng sử dụng tên đăng nhập khác.");
        }

        // Kiểm tra email
        if (isEmailExist(user.getEmail())) {
            throw new EmailAlreadyExistsException("Vui lòng sử dụng email khác.");
        }

        // Kiểm tra mật khẩu xác nhận
        if (!user.getPassword().equals(user.getConfirmPassword())) {
            throw new PasswordValidationException("Mật khẩu xác nhận không khớp");
        }

        // Kiểm tra độ dài mật khẩu
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
}
