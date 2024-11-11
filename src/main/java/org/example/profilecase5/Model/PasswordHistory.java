package org.example.profilecase5.Model;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "passwordhistory")
public class PasswordHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private int historyId;

    @Column(name = "user_id", nullable = false)
    private int userId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)  // Thiết lập quan hệ với lớp User
    private User user;

    @Column(name = "old_password_hash", nullable = false)
    private String oldPasswordHash;

    @Column(name = "new_password_hash", nullable = false)
    private String newPasswordHash;

    @Column(name = "confirm_password", nullable = false)
    private String confirmPassword;

    @Column(name = "changed_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp changedAt;

    // Getter và setter cho các trường dữ liệu
    public int getHistoryId() {
        return historyId;
    }

    public void setHistoryId(int historyId) {
        this.historyId = historyId;
    }

    public int getUserId() {
        return userId; // Getter cho trường user_id
    }

    public void setUserId(int userId) {
        this.userId = userId; // Setter cho trường user_id
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getOldPasswordHash() {
        return oldPasswordHash;
    }

    public void setOldPasswordHash(String oldPasswordHash) {
        this.oldPasswordHash = oldPasswordHash;
    }

    public String getNewPasswordHash() {
        return newPasswordHash;
    }

    public void setNewPasswordHash(String newPasswordHash) {
        this.newPasswordHash = newPasswordHash;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public Timestamp getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(Timestamp changedAt) {
        this.changedAt = changedAt;
    }
}
