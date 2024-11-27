package org.example.profilecase5.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "house")
public class House {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "house_id")
    private int houseId; // Khóa chính, tự động tăng

    @Column(name = "property_name", nullable = false)
    @NotEmpty(message = "Tên căn nhà không được để trống")
    @Size(max = 255, message = "Tên căn nhà không được vượt quá 255 ký tự")
    private String propertyName; // Tên căn nhà

    @Column(name = "address", nullable = false)
    @NotEmpty(message = "Địa chỉ không được để trống")
    @Size(max = 255, message = "Địa chỉ không được vượt quá 255 ký tự")
    private String address; // Địa chỉ

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "ENUM('available', 'rented', 'closed')", nullable = false)
    @NotNull(message = "Trạng thái không được để trống")
    private Status status; // Trạng thái

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "bedrooms", nullable = false)
    @Min(value = 1, message = "Số lượng phòng ngủ phải ít nhất là 1")
    @Max(value = 10, message = "Số lượng phòng ngủ tối đa là 10")
    private int bedrooms; // Số phòng ngủ

    @Column(name = "bathrooms", nullable = false)
    @Min(value = 1, message = "Số lượng phòng tắm phải ít nhất là 1")
    @Max(value = 3, message = "Số lượng phòng tắm tối đa là 3")
    private int bathrooms; // Số phòng tắm

    @Column(name = "description", columnDefinition = "TEXT")
    @Size(max = 1000, message = "Mô tả không được vượt quá 1000 ký tự")
    private String description; // Mô tả chung
    @Column(name = "rental_count", nullable = false)
    private int rentalCount = 0; // Số lượt thuê, mặc định là 0

    @Column(name = "price_per_day", nullable = false)
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá tiền phải lớn hơn 0")
    @Digits(integer = 10, fraction = 2, message = "Giá tiền không hợp lệ")
    private BigDecimal pricePerDay; // Giá tiền theo ngày

    
    @OneToMany(mappedBy = "house", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HouseImage> houseImages = new ArrayList<>();

    // Mối quan hệ với bảng User
    @ManyToOne
    @JoinColumn(name = "user_id")  // tên cột trong cơ sở dữ liệu
    private User user;

    // Enum for Status
    public enum Status {
        available, rented
        , closed
    }

    // Getters và Setters
    public int getHouseId() {
        return houseId;
    }

    public void setHouseId(int houseId) {
        this.houseId = houseId;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Status getStatus() {
        return status;
    }

    public List<HouseImage> getHouseImages() {
        return houseImages;
    }

    public void setHouseImages(List<HouseImage> houseImages) {
        this.houseImages = houseImages;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public int getBedrooms() {
        return bedrooms;
    }

    public void setBedrooms(int bedrooms) {
        this.bedrooms = bedrooms;
    }

    public int getBathrooms() {
        return bathrooms;
    }

    public void setBathrooms(int bathrooms) {
        this.bathrooms = bathrooms;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(BigDecimal pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
