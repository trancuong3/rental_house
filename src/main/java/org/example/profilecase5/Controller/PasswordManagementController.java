package org.example.profilecase5.Controller;

import org.example.profilecase5.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/password")
public class PasswordManagementController {
    @Autowired
    private UserService userService;

    @PostMapping("/encrypt-all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> encryptAllPasswords() {
        try {
            userService.encryptAllPasswords();
            return ResponseEntity.ok("All passwords have been encrypted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error encrypting passwords: " + e.getMessage());
        }
    }
}
