package org.example.profilecase5.Service;

import org.example.profilecase5.Model.House;
import org.example.profilecase5.Model.RentalHistory;
import org.example.profilecase5.Model.RentalNotificationDTO;
import org.example.profilecase5.Model.User;
import org.example.profilecase5.Repository.RentalHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RentalHistoryService {

    @Autowired
    private RentalHistoryRepository rentalHistoryRepository;

    // Method to get rental history by house ID
    public List<RentalHistory> getRentalHistoriesByHouseId(int houseId) {
        return rentalHistoryRepository.findByHouse_HouseId(houseId);
    }

    public List<RentalHistory> getAllRentalHistory() {
        return rentalHistoryRepository.findAll();
    }

    public RentalHistory getRentalHistoryById(int id) {
        return rentalHistoryRepository.findByRentalId(id);
    }

    public void save(RentalHistory rentalHistory) {
        rentalHistoryRepository.save(rentalHistory);
    }

    public void checkIn(RentalHistory rentalHistory) {
        rentalHistory.setStatus(RentalHistory.RentalStatus.Checked_in);
        rentalHistoryRepository.save(rentalHistory);
    }

    public void checkOut(RentalHistory rentalHistory) {
        rentalHistory.setStatus(RentalHistory.RentalStatus.Checked_out);
        rentalHistoryRepository.save(rentalHistory);
    }

    public Page<RentalHistory> getAllRentalHistory(Pageable pageable) {
        return rentalHistoryRepository.findAll(pageable);
    }

    public void cancel(RentalHistory rentalHistory) {
        rentalHistory.setStatus(RentalHistory.RentalStatus.Cancelled);
        rentalHistoryRepository.save(rentalHistory);
    }

    public void book (RentalHistory rentalHistory) {
        rentalHistory.setStatus(RentalHistory.RentalStatus.Pending);
        rentalHistoryRepository.save(rentalHistory);
    }

    public Page<RentalHistory> searchRentalHistories(List<House> houses, String propertyName, Timestamp startDate, Timestamp endDate, RentalHistory.RentalStatus status, Pageable pageable) {
        if (houses == null || houses.isEmpty()) {
            return Page.empty(pageable);
        }

        List<Integer> houseIds = houses.stream()
                .map(House::getHouseId)
                .collect(Collectors.toList());
        return rentalHistoryRepository.searchRentalHistories(houseIds, propertyName, startDate, endDate, status, pageable);
    }

    public Page<RentalHistory> getRentalHistoriesByHouses(List<House> houses, Pageable pageable) {
        return rentalHistoryRepository.findByHouseIn(houses, pageable);
    }

    public Page<RentalNotificationDTO> getLatestNotifications(Pageable pageable) {
        return rentalHistoryRepository.findLatestNotifications(pageable);
    }

    // ====================================================================
    // ======= PHƯƠNG THỨC BỔ SUNG CHO ADMINCONTROLLER =======
    // ====================================================================

    /**
     * Phương thức này sửa lỗi: cannot find symbol method getRentalHistoryByUserId(int)
     * Lấy danh sách lịch sử thuê nhà theo ID người dùng thuê.
     */
    public List<RentalHistory> getRentalHistoryByUserId(int userId) {
        // Gọi phương thức mới findByUser_UserId trong Repository
        return rentalHistoryRepository.findByUser_UserId(userId);
    }

    /**
     * Phương thức này tính tổng tiền đã chi tiêu của User.
     * Đã cập nhật để gọi phương thức Repository mới (sumTotalPriceByUserId).
     */
    public double calculateTotalSpentByUser(int userId) {
        return rentalHistoryRepository.sumTotalPriceByUserId(userId);
    }
}