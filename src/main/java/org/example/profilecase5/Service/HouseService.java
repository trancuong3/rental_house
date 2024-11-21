package org.example.profilecase5.Service;

import org.example.profilecase5.Model.House;
import org.example.profilecase5.Repository.HouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HouseService {

    @Autowired
    private HouseRepository houseRepository;

    // Phương thức lưu nhà vào cơ sở dữ liệu
    public void saveHouse(House house) {
        houseRepository.save(house);
    }
    public List<House> getHousesByUserId(int userId) {
        return houseRepository.findByUser_UserId(userId);

    }
}
