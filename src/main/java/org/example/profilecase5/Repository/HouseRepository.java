package org.example.profilecase5.Repository;

import org.example.profilecase5.Model.House;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Repository
public interface HouseRepository extends JpaRepository<House, Integer> {
    List<House> findByUser_UserId(int userId);
    Optional<House> findByHouseId(int houseId);
    Optional<House> findById(Integer id);

    List<House> findTop5ByOrderByRentalCountDesc();
    List<House> findByPropertyNameContainingIgnoreCase(String propertyName);
    List<House> findByStatus(House.Status status);
    Page<House> findByUser_UserId(int userId, Pageable pageable);
}
