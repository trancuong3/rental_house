package org.example.profilecase5.Model;

import java.sql.Timestamp;

public class RentalNotificationDTO {
    private String userName;
    private String houseName;
    private Timestamp startDate;

    public RentalNotificationDTO(String userName, String houseName, Timestamp startDate) {
        this.userName = userName;
        this.houseName = houseName;
        this.startDate = startDate;
    }

    // Getter/Setter
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getHouseName() {
        return houseName;
    }

    public void setHouseName(String houseName) {
        this.houseName = houseName;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }
}
