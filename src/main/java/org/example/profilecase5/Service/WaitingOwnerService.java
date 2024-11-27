package org.example.profilecase5.Service;

import org.example.profilecase5.Exception.User.EmailAlreadyExistsException;
import org.example.profilecase5.Exception.User.PasswordValidationException;
import org.example.profilecase5.Exception.User.UsernameAlreadyExistsException;
import org.example.profilecase5.Model.Role;
import org.example.profilecase5.Model.User;
import org.example.profilecase5.Model.WaitingOwner;
import org.example.profilecase5.Repository.RoleRepository;
import org.example.profilecase5.Repository.UserRepository;
import org.example.profilecase5.Repository.WaitingOwnerRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Service
public class WaitingOwnerService extends UserService{
    private final UserService userService;
    private final WaitingOwnerRepository waitingOwnerRepository;

    public WaitingOwnerService(UserRepository userRepository, PasswordEncoder passwordEncoder, WaitingOwnerRepository waitingOwnerRepository, RoleRepository roleRepository, @Qualifier("userService") UserService userService) {
        super(userRepository, passwordEncoder, roleRepository);
        this.userService = userService;
        this.waitingOwnerRepository = waitingOwnerRepository;
    }

    public void addWaitingOwner(WaitingOwner waitingOwner) {
        if (isUsernameExist(waitingOwner.getUsername())) {
            throw new UsernameAlreadyExistsException("Vui lòng sử dụng tên đăng nhập khác.");
        }

        // Kiểm tra email
        if (isEmailExist(waitingOwner.getEmail())) {
            throw new EmailAlreadyExistsException("Vui lòng sử dụng email khác.");
        }

        // Kiểm tra mật khẩu xác nhận
        if (!waitingOwner.getPassword().equals(waitingOwner.getConfirmPassword())) {
            throw new PasswordValidationException("Mật khẩu xác nhận không khớp");
        }

        // Kiểm tra độ dài mật khẩu
        if (waitingOwner.getPassword().length() < 6 || waitingOwner.getPassword().length() > 32) {
            throw new PasswordValidationException("Mật khẩu phải có độ dài từ 6 đến 32 ký tự");
        }

        Timestamp currentTimestamp = Timestamp.from(Instant.now());
        waitingOwner.setCreatedAt(currentTimestamp);
        waitingOwner.setUpdatedAt(currentTimestamp);
        waitingOwner.setPassword(passwordEncoder.encode(waitingOwner.getPassword()));
        waitingOwner.setConfirmPassword(passwordEncoder.encode(waitingOwner.getConfirmPassword()));

        Role userRole = roleRepository.findByRoleName("ROLE_OWNER")
                .orElseThrow(() -> new RuntimeException("Role không tồn tại"));
        waitingOwner.setRole(userRole);
        waitingOwnerRepository.save(waitingOwner);
        encryptAllPasswords();
    }
    public List<WaitingOwner> getAllWaitingOwners() {
        return waitingOwnerRepository.findAll();
    }
    public void acceptWaitingOwner(int id) {
        WaitingOwner waitingOwner = waitingOwnerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Waiting owner not found"));

        User user = new User();
        user.setUsername(waitingOwner.getUsername());
        user.setEmail(waitingOwner.getEmail());
        user.setPhone(waitingOwner.getPhone());
        user.setFullname(waitingOwner.getFullname());
        user.setAvatar(waitingOwner.getAvatar());
        user.setAddress(waitingOwner.getAddress() != null ? waitingOwner.getAddress() : "Chưa cung cấp");
        user.setStatus(waitingOwner.getStatus());
        user.setCreatedAt(waitingOwner.getCreatedAt());
        user.setUpdatedAt(waitingOwner.getUpdatedAt());
        user.setPassword(waitingOwner.getPassword());
        user.setConfirmPassword(waitingOwner.getConfirmPassword());
        user.setRole(waitingOwner.getRole());

        userRepository.save(user);
        waitingOwnerRepository.delete(waitingOwner);
    }




    public void refuseWaitingOwner(int userId) {
        WaitingOwner owner = waitingOwnerRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("WaitingOwner không tồn tại"));
        waitingOwnerRepository.delete(owner);
    }


}
