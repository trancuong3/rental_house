package org.example.profilecase5.Service;

import org.example.profilecase5.Model.User;
import org.example.profilecase5.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
public class CustomerUserDetailService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Lấy User từ repository
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        // Kiểm tra mật khẩu chưa mã hóa và mã hóa nếu cần
        if (!isPasswordEncrypted(user.getPassword())) {
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
            user.setConfirmPassword(encodedPassword);
            userRepository.save(user);
        }
        if (user.getStatus() == User.Status.Locked) {
            throw new UsernameNotFoundException("Tài khoản đã bị khóa");
        }

        // Trả về đối tượng UserDetails, với vai trò là một đối tượng Role duy nhất
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole().getRoleName())) // Lấy Role duy nhất
        );
    }

    private boolean isPasswordEncrypted(String password) {
        return password != null && password.startsWith("$2a$");
    }
}
