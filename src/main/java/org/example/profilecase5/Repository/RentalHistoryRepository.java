package org.example.profilecase5.Repository;

import org.example.profilecase5.Model.House;
import org.example.profilecase5.Model.RentalHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import java.sql.Timestamp;
import java.util.List;

@Repository
public interface RentalHistoryRepository extends JpaRepository<RentalHistory, Integer> {
    RentalHistory findByRentalId(int rentalId);
    List<RentalHistory> findByHouseIn(List<House> houses);
    List<RentalHistory> findByHouse_HouseId(int houseId);
    @Query("SELECT rh FROM RentalHistory rh " +
            "JOIN rh.house h " +
            "WHERE (:propertyName IS NULL OR LOWER(h.propertyName) LIKE LOWER(CONCAT('%', :propertyName, '%'))) " +
            "AND (:startDate IS NULL OR rh.startDate >= :startDate) " +
            "AND (:endDate IS NULL OR rh.endDate <= :endDate) " +
            "AND (:status IS NULL OR rh.status = :status) " +
            "AND (h.user.userId = :userId)")
    Page<RentalHistory> searchRentalHistories(
            @Param("propertyName") String propertyName,
            @Param("startDate") Timestamp startDate,
            @Param("endDate") Timestamp endDate,
            @Param("status") RentalHistory.RentalStatus status,
            @Param("userId") int userId,
            Pageable pageable);
}
