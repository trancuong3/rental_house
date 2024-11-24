package org.example.profilecase5.Repository;

import org.example.profilecase5.Model.HouseImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface HouseImageRepository extends JpaRepository<HouseImage, Integer> {

    @Query("SELECT i FROM HouseImage i WHERE i.house.houseId = :houseId")
    List<HouseImage> findByHouseId(@Param("houseId") Integer houseId);

    @Query("SELECT hi FROM HouseImage hi WHERE hi.isMain = true")
    List<HouseImage> findMainImages();

}
