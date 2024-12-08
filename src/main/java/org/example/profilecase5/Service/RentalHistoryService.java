package org.example.profilecase5.Service;

import org.example.profilecase5.Model.House;
import org.example.profilecase5.Model.RentalHistory;
import org.example.profilecase5.Model.User;
import org.example.profilecase5.Repository.RentalHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RentalHistoryService {

    @Autowired
    private RentalHistoryRepository rentalHistoryRepository;  // Inject repository

    // Method to get rental history by house ID
    public List<RentalHistory> getRentalHistoriesByHouseId(int houseId) {
        return rentalHistoryRepository.findByHouse_HouseId(houseId);  // Assume findByHouseId is implemented in the repository
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
}