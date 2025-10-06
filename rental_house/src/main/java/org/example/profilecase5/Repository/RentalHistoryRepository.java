package org.example.profilecase5.Repository;

import org.example.profilecase5.Model.House;
import org.example.profilecase5.Model.RentalHistory;
import org.example.profilecase5.Model.RentalNotificationDTO;
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
    List<RentalHistory> findByHouse_HouseId(int houseId);
    Page<RentalHistory> findByHouseIn(List<House> houses, Pageable pageable);
    @Query("SELECT rh FROM RentalHistory rh " +
            "WHERE (rh.house.houseId IN :houseIds) " +
            "AND (:propertyName IS NULL OR LOWER(rh.house.propertyName) LIKE LOWER(CONCAT('%', :propertyName, '%'))) " +
            "AND (:startDate IS NULL OR rh.startDate >= :startDate) " +
            "AND (:endDate IS NULL OR rh.endDate <= :endDate) " +
            "AND (:status IS NULL OR rh.status = :status)")
    Page<RentalHistory> searchRentalHistories(
            @Param("houseIds") List<Integer> houseIds,
            @Param("propertyName") String propertyName,
            @Param("startDate") Timestamp startDate,
            @Param("endDate") Timestamp endDate,
            @Param("status") RentalHistory.RentalStatus status,
            Pageable pageable);

    @Query("SELECT new org.example.profilecase5.Model.RentalNotificationDTO(r.user.username, r.house.propertyName, r.startDate) " +
            "FROM RentalHistory r ORDER BY r.startDate DESC")
    Page<RentalNotificationDTO> findLatestNotifications(Pageable pageable);
}
