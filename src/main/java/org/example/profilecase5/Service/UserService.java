package org.example.profilecase5.Service;
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

            //Check xem pass da duoc ma hoa chua
            if (!isPasswordEncrypted(user.getPassword())) {

                //neu chua ma hoa, ma hoa no va cap nhat vao dtb
                String encodedPassword = passwordEncoder.encode(user.getPassword());
                user.setPassword(encodedPassword);
                user.setConfirmPassword(encodedPassword);
                userRepository.save(user);
            }

            //check pass va role
            boolean passwordMatches = passwordEncoder.matches(password, user.getPassword());
            boolean hasRole = user.getRoles().stream()
                    .anyMatch(role -> role.getRoleName().equalsIgnoreCase("ROLE_" + selectedRole));

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
