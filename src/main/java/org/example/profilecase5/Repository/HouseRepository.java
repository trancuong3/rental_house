package org.example.profilecase5.Repository;

import org.example.profilecase5.Model.House;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HouseRepository extends JpaRepository<House, Integer> {
    List<House> findByUser_UserId(int userId);

    Optional<House> findById(Integer id);
}
