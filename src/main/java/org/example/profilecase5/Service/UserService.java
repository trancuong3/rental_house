package org.example.profilecase5.Service;

import org.example.profilecase5.Model.User;
import org.example.profilecase5.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User getUserById(int userId) {
        return userRepository.findById(userId).orElse(null);
    }
    public void updateUser(User user) {
        userRepository.save(user);
    }


}
