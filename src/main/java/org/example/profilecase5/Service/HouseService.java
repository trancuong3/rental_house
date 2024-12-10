package org.example.profilecase5.Service;

import jakarta.transaction.Transactional;
import org.example.profilecase5.Model.House;
import org.example.profilecase5.Model.HouseImage;
import org.example.profilecase5.Repository.HouseImageRepository;
import org.example.profilecase5.Repository.HouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class HouseService {

    @Autowired
    private HouseRepository houseRepository;
    @Autowired
    private HouseImageRepository houseImageRepository;

    // Phương thức lưu nhà vào cơ sở dữ liệu
    public void saveHouse(House house) {
        houseRepository.save(house);
    }

    public List<House> getHousesByUserId(int userId) {
        return houseRepository.findByUser_UserId(userId);
    }

    public Optional<House> findById(int id) {
        return houseRepository.findById(id);
    }

    @Transactional
    public void updateHouse(House house, MultipartFile image) throws Exception {
        House existingHouse = houseRepository.findById(house.getHouseId())
                .orElseThrow(() -> new Exception("House không tồn tại"));

        // Cập nhật thông tin của house
        existingHouse.setPropertyName(house.getPropertyName());
        existingHouse.setAddress(house.getAddress());
        existingHouse.setStatus(house.getStatus());
        existingHouse.setBedrooms(house.getBedrooms());
        existingHouse.setBathrooms(house.getBathrooms());
        existingHouse.setPricePerDay(house.getPricePerDay());
        existingHouse.setDescription(house.getDescription());

        // Nếu có ảnh, thêm vào danh sách ảnh
        if (!house.getHouseImages().isEmpty()) {
            for (HouseImage houseImage : house.getHouseImages()) {
                if (houseImage.getImageUrl() != null && !houseImage.getImageUrl().isEmpty()) {
                    houseImageRepository.save(houseImage);
                }
            }
        }

        // Lưu lại đối tượng house vào cơ sở dữ liệu
        houseRepository.save(existingHouse);
    }


    // Phương thức từ nhánh main
    public List<HouseImage> getMainImages() {
        return houseImageRepository.findMainImages();
    }

    public House getHouseById(Integer id) {
        return houseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Have not found"));
    }

    public List<House> searchHousesByName(String propertyName) {
        return houseRepository.findByPropertyNameContainingIgnoreCase(propertyName);
    }
    public List<House> getAllHouses() {
        return houseRepository.findAll();
    }
    public List<House> getTop5MostRentedHouses() {
        return houseRepository.findTop5ByOrderByRentalCountDesc();
    }
    public List<House> searchHousesByStatus(House.Status status) {
        return houseRepository.findByStatus(status);
    }
    public List<HouseImage> getImagesByHouseId(Integer houseId) {
        return houseImageRepository.findByHouseId(houseId);
    }
    public Page<House> getHousesByUserId(int userId, int page, int size) {
        return houseRepository.findByUser_UserId(userId, PageRequest.of(page, size));
    }

    public Page<HouseImage> getMainImages(Pageable pageable) {
        return houseImageRepository.findAll(pageable);
    }
    // Tìm kiếm nhà theo tên của chủ nhà hiện tại
    public List<House> searchHousesByNameAndUser(String propertyName, int userId) {
        return houseRepository.findByPropertyNameContainingIgnoreCaseAndUser_UserId(propertyName, userId);
    }

    // Tìm kiếm nhà theo trạng thái của chủ nhà hiện tại
    public List<House> searchHousesByStatusAndUser(House.Status status, int userId) {
        return houseRepository.findByStatusAndUser_UserId(status, userId);
    }

    // Lấy tất cả nhà của chủ nhà hiện tại



}