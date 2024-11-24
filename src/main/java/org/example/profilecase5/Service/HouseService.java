package org.example.profilecase5.Service;

import jakarta.transaction.Transactional;
import org.example.profilecase5.Model.House;
import org.example.profilecase5.Model.HouseImage;
import org.example.profilecase5.Repository.HouseImageRepository;
import org.example.profilecase5.Repository.HouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
        // Lấy đối tượng house từ cơ sở dữ liệu để cập nhật
        House existingHouse = houseRepository.findById(house.getHouseId())
                .orElseThrow(() -> new Exception("House không tồn tại"));

        // Cập nhật thông tin từ đối tượng house được truyền vào
        existingHouse.setPropertyName(house.getPropertyName());
        existingHouse.setAddress(house.getAddress());
        existingHouse.setStatus(house.getStatus());
        existingHouse.setBedrooms(house.getBedrooms());
        existingHouse.setBathrooms(house.getBathrooms());
        existingHouse.setPricePerDay(house.getPricePerDay());
        existingHouse.setDescription(house.getDescription());

        // Nếu có ảnh mới, thêm ảnh mà không xóa ảnh cũ
        if (image != null && !image.isEmpty()) {
            try {
                // Chuyển ảnh thành mảng byte
                byte[] imageBytes = image.getBytes();

                // Mã hóa mảng byte thành chuỗi Base64
                String base64Image = Base64.getEncoder().encodeToString(imageBytes);

                // Tạo đối tượng HouseImage để lưu vào cơ sở dữ liệu
                HouseImage newImage = new HouseImage();
                newImage.setHouse(existingHouse);
                newImage.setImageUrl(base64Image); // Lưu chuỗi Base64 vào imageUrl
                newImage.setMain(false); // Đánh dấu ảnh này không phải ảnh chính

                // Lưu ảnh vào bảng HouseImage
                houseImageRepository.save(newImage);
            } catch (IOException e) {
                throw new Exception("Đã xảy ra lỗi khi xử lý ảnh: " + e.getMessage(), e);
            }
        }

        // Lưu lại đối tượng house đã chỉnh sửa
        houseRepository.save(existingHouse);
    }
    public List<House> getAllHouses() {
        return houseRepository.findAll();
    }
    public List<House> getTop5MostRentedHouses() {
        return houseRepository.findTop5ByOrderByRentalCountDesc();
    }

}
