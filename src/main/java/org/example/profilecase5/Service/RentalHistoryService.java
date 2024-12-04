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
}