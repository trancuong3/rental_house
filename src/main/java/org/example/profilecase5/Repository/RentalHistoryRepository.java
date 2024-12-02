package org.example.profilecase5.Repository;

import org.example.profilecase5.Model.RentalHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentalHistoryRepository extends JpaRepository<RentalHistory, Integer> {
    RentalHistory findByRentalId(int rentalId);
    List<RentalHistory> findByHouse_HouseId(int houseId);
}
