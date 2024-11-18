package org.example.profilecase5.Exception.User;

public class PasswordValidationException extends RuntimeException {

    // Constructor nhận thông báo lỗi
    public PasswordValidationException(String message) {
        super(message);
    }
}