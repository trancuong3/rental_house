package org.example.profilecase5.Service;

import org.example.profilecase5.Exception.User.UsernameAlreadyExistsException;
import org.example.profilecase5.Model.User;
import org.example.profilecase5.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public User getUserById(int userId) {
        return userRepository.findById(userId).orElse(null);
    }
    public void updateUser(User user) {
        userRepository.save(user);
    }
    public void registerUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            // Handle username already exists
            throw new UsernameAlreadyExistsException("Tên người dùng đã tồn tại");
        }

        userRepository.save(user);
    }
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void createUser(User user) {
        // Mã hóa passwor
        // d trước khi lưu
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }
    public boolean validateUserAndRole(String username, String password, String selectedRole) {
        User user = userRepository.findByUsername(username)
                .orElse(null);

        if (user == null) {
            return false;
        }

        // Kiểm tra password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return false;
        }

        // Kiểm tra role
        return user.getRoles().stream()
                .anyMatch(role -> {
                    if ("user".equals(selectedRole)) {
                        return role.getRoleName().equals("ROLE_USER");
                    } else {
                        return role.getRoleName().equals("ROLE_ADMIN");
                    }
                });
    }

    
}
