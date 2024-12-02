package org.example.profilecase5.Service;

import org.example.profilecase5.Model.RentalHistory;
import org.example.profilecase5.Repository.RentalHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RentalHistoryService {

    @Autowired
    private RentalHistoryRepository rentalHistoryRepository;

    public List<RentalHistory> getAllRentalHistory() {
        return rentalHistoryRepository.findAll();
    }

    public RentalHistory getRentalHistoryById(int id) {
        return rentalHistoryRepository.findByRentalId(id);
    }
}
