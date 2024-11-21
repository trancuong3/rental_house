package org.example.profilecase5.Controller;

import net.coobird.thumbnailator.Thumbnails;
import org.example.profilecase5.Model.House;
import org.example.profilecase5.Model.HouseImage;
import org.example.profilecase5.Model.User;
import org.example.profilecase5.Service.HouseService;
import org.example.profilecase5.Service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Controller
@RequestMapping("/house")
public class HouseController {

    @Autowired
    private HouseService houseService;

    @Autowired
    private UserService userService;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final Logger logger = LoggerFactory.getLogger(HouseController.class);
    private final String UPLOAD_DIR = "uploads/";

    @GetMapping("/new")
    public String showHouseForm(Model model) {
        model.addAttribute("house", new House());
        return "house/house_form";
    }

    @PostMapping("/new")
    public String saveHouse(@Valid @ModelAttribute("house") House house,
                            BindingResult bindingResult,
                            @RequestParam("image") MultipartFile[] imageFiles, // Dùng mảng MultipartFile để hỗ trợ tải lên nhiều ảnh
                            Model model) {

        // Kiểm tra lỗi validation
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            logger.warn("Validation errors while submitting the form: {}", bindingResult.getAllErrors());
            return "house/house_form";
        }

        // Kiểm tra nếu không có ảnh nào được chọn
        if (imageFiles.length == 0) {
            model.addAttribute("errorMessage", "Hãy chọn ít nhất một hình ảnh để tải lên.");
            logger.warn("No image files selected by the user.");
            return "house/house_form";
        }

        // Danh sách các tên ảnh đã tải lên
        List<String> imageNames = new ArrayList<>();

        // Duyệt qua các ảnh được chọn và xử lý từng ảnh
        for (MultipartFile imageFile : imageFiles) {
            if (imageFile.isEmpty()) {
                continue;
            }

            try {
                // Kiểm tra kích thước ảnh
                if (imageFile.getSize() > MAX_FILE_SIZE) {
                    model.addAttribute("errorMessage", "Ảnh quá lớn, vui lòng chọn ảnh nhỏ hơn 5MB.");
                    logger.warn("File size exceeds the limit: {} bytes", imageFile.getSize());
                    return "house/house_form";
                }

                // Tạo thumbnail cho ảnh (giảm kích thước ảnh nếu cần)
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                Thumbnails.of(imageFile.getInputStream())
                        .size(800, 800) // Resize ảnh về kích thước phù hợp
                        .outputFormat("JPEG")
                        .outputQuality(0.8f)
                        .toOutputStream(outputStream);

                byte[] resizedImage = outputStream.toByteArray();
                String base64Image = Base64.getEncoder().encodeToString(resizedImage);

                // Tạo đối tượng HouseImage và gắn vào House
                HouseImage houseImage = new HouseImage();
                houseImage.setImageUrl(base64Image);
                houseImage.setHouse(house);

                // Nếu đây là ảnh đầu tiên thì đánh dấu là ảnh chính
                if (house.getHouseImages().isEmpty()) {
                    houseImage.setMain(true);
                } else {
                    houseImage.setMain(false);
                }

                house.getHouseImages().add(houseImage);
                imageNames.add(imageFile.getOriginalFilename()); // Thêm tên ảnh vào danh sách

            } catch (IOException e) {
                logger.error("Error while processing image file", e);
                model.addAttribute("errorMessage", "Đã xảy ra lỗi khi xử lý ảnh. Vui lòng thử lại.");
                return "house/house_form";
            }
        }

        // Lấy thông tin người dùng hiện tại
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByUsername(authentication.getName());
        house.setUser(user);

        // Lưu house vào cơ sở dữ liệu
        try {
            houseService.saveHouse(house);
            logger.info("House saved successfully: {}", house);
        } catch (Exception e) {
            logger.error("Error while saving house to the database", e);
            model.addAttribute("errorMessage", "Có lỗi xảy ra khi lưu dữ liệu nhà.");
            return "house/house_form";
        }

        return "redirect:/hosting/listings"; // Đảm bảo có một trang thành công hiển thị thông tin sau khi lưu
    }
}
