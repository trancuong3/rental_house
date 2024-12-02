package org.example.profilecase5.Service;

import org.example.profilecase5.Model.RentalHistory;
import org.example.profilecase5.Repository.RentalHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RentalHistoryService {

    @Autowired
    private RentalHistoryRepository rentalHistoryRepository;  // Inject repository

    // Method to get rental history by house ID
    public List<RentalHistory> getRentalHistoriesByHouseId(int houseId) {
        return rentalHistoryRepository.findByHouse_HouseId(houseId);  // Assume findByHouseId is implemented in the repository
    }
}