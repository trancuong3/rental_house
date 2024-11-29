package org.example.profilecase5.Controller;

import net.coobird.thumbnailator.Thumbnails;
import org.example.profilecase5.Model.House;
import org.example.profilecase5.Model.HouseImage;
import org.example.profilecase5.Model.User;
import org.example.profilecase5.Service.HouseService;
import org.example.profilecase5.Service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import java.net.URL;
import java.io.InputStream;
import java.io.ByteArrayInputStream;

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
import java.util.Optional;

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
    public String showHouseForm(Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.getUserByUsername(username);

        if (user.getAvatar() == null) {
            user.setAvatar("/images/img_2.png"); // Cung cấp ảnh mặc định nếu không có avatar
        }

        model.addAttribute("user", user); // Thêm đối tượng user vào model
        model.addAttribute("house", new House());
        return "house/house_form";
    }


    @PostMapping("/new")
    public String saveHouse(@Valid @ModelAttribute("house") House house,
                            BindingResult bindingResult,
                            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                            @RequestParam(value = "imageUrl", required = false) String imageUrl,
                            Model model) {

        // Kiểm tra lỗi validation
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            logger.warn("Validation errors while submitting the form: {}", bindingResult.getAllErrors());
            return "house/house_form";
        }

        // Kiểm tra người dùng chỉ chọn một phương thức tải ảnh
        if ((imageFile == null || imageFile.isEmpty()) && (imageUrl == null || imageUrl.trim().isEmpty())) {
            model.addAttribute("errorMessage", "Vui lòng tải lên ảnh hoặc nhập URL.");
            return "house/house_form";
        }

        if ((imageFile != null && !imageFile.isEmpty()) && (imageUrl != null && !imageUrl.trim().isEmpty())) {
            model.addAttribute("errorMessage", "Chỉ được chọn một trong hai: tải lên ảnh hoặc nhập URL.");
            return "house/house_form";
        }

        try {
            // Xử lý ảnh từ file
            if (imageFile != null && !imageFile.isEmpty()) {
                if (imageFile.getSize() > MAX_FILE_SIZE) {
                    model.addAttribute("errorMessage", "Ảnh quá lớn, vui lòng chọn ảnh nhỏ hơn 5MB.");
                    logger.warn("File size exceeds the limit: {} bytes", imageFile.getSize());
                    return "house/house_form";
                }

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                Thumbnails.of(imageFile.getInputStream())
                        .size(800, 800)
                        .outputFormat("JPEG")
                        .outputQuality(0.8f)
                        .toOutputStream(outputStream);

                byte[] resizedImage = outputStream.toByteArray();
                String base64Image = Base64.getEncoder().encodeToString(resizedImage);

                HouseImage houseImage = new HouseImage();
                houseImage.setImageUrl(base64Image);
                houseImage.setHouse(house);
                houseImage.setMain(true);

                house.getHouseImages().add(houseImage);
            }

            // Xử lý ảnh từ URL
            if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                if (imageUrl.startsWith("data:")) {
                    // Nếu là data URL, xử lý nó như một chuỗi Base64
                    try {
                        String base64Image = imageUrl.split(",")[1];  // Tách phần Base64 từ data URL
                        byte[] imageBytes = Base64.getDecoder().decode(base64Image);

                        // Xử lý ảnh từ chuỗi Base64
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        Thumbnails.of(new ByteArrayInputStream(imageBytes))
                                .size(800, 800)
                                .outputFormat("JPEG")
                                .outputQuality(0.8f)
                                .toOutputStream(outputStream);

                        byte[] resizedImage = outputStream.toByteArray();
                        String resizedBase64Image = Base64.getEncoder().encodeToString(resizedImage);

                        HouseImage houseImage = new HouseImage();
                        houseImage.setImageUrl(resizedBase64Image);
                        houseImage.setHouse(house);
                        houseImage.setMain(true);

                        house.getHouseImages().add(houseImage);
                    } catch (Exception e) {
                        logger.error("Error while processing data URL", e);
                        model.addAttribute("errorMessage", "Đã xảy ra lỗi khi xử lý ảnh từ URL.");
                        return "house/house_form";
                    }
                } else {
                    // Nếu không phải data URL, xử lý như bình thường
                    try {
                        URL url = new URL(imageUrl);
                        InputStream inputStream = url.openStream();

                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        Thumbnails.of(inputStream)
                                .size(800, 800)
                                .outputFormat("JPEG")
                                .outputQuality(0.8f)
                                .toOutputStream(outputStream);

                        byte[] resizedImage = outputStream.toByteArray();
                        String base64Image = Base64.getEncoder().encodeToString(resizedImage);

                        HouseImage houseImage = new HouseImage();
                        houseImage.setImageUrl(base64Image);
                        houseImage.setHouse(house);
                        houseImage.setMain(true);

                        house.getHouseImages().add(houseImage);
                    } catch (IOException e) {
                        logger.error("Error while processing image URL", e);
                        model.addAttribute("errorMessage", "Đã xảy ra lỗi khi xử lý ảnh từ URL.");
                        return "house/house_form";
                    }
                }
            }

        } catch (IOException e) {
            logger.error("Error while processing image file or URL", e);
            model.addAttribute("errorMessage", "Đã xảy ra lỗi khi xử lý ảnh. Vui lòng thử lại.");
            return "house/house_form";
        }

        // Gán thông tin người dùng hiện tại
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByUsername(authentication.getName());
        if (user.getAvatar() == null || user.getAvatar().isEmpty()) {
            user.setAvatar("/images/img_2.png");
        }

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

        return "redirect:/hosting/listings";
    }




    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") int id, Model model, Authentication authentication) {
        if (authentication == null) {
            model.addAttribute("errorMessage", "Bạn cần đăng nhập để chỉnh sửa");
            return "redirect:/login"; // Quay về trang đăng nhập nếu chưa đăng nhập
        }

        String username = authentication.getName();  // Lấy tên người dùng từ Authentication
        User user = userService.getUserByUsername(username);

        if (user == null) {
            model.addAttribute("errorMessage", "Người dùng không tồn tại");
            return "redirect:/login"; // Quay về trang đăng nhập nếu không tìm thấy người dùng
        }

        if (user.getAvatar() == null) {
            user.setAvatar("/images/img_2.png"); // Cung cấp ảnh mặc định nếu không có avatar
        }

        model.addAttribute("user", user); // Lấy người dùng từ dịch vụ

        Optional<House> house = houseService.findById(id);
        if (house.isPresent()) {
            model.addAttribute("house", house.get());
            return "house/edit"; // Tên view chỉnh sửa nhà.
        } else {
            model.addAttribute("errorMessage", "Không tìm thấy nhà với ID: " + id);
            return "redirect:/house/list"; // Quay lại danh sách nếu không tìm thấy.
        }
    }

    @PostMapping("/edit")
    public String editHouse(@ModelAttribute("house") House house,
                            @RequestParam(value = "image", required = false) MultipartFile image,
                            @RequestParam(value = "imageUrl", required = false) String imageUrl,
                            @RequestParam(value = "houseId", required = false) Integer houseId,
                            Model model) {

        try {
            // Kiểm tra houseId và gán nếu cần
            if (house.getHouseId() == 0 && houseId != null) {
                house.setHouseId(houseId);
            }

            // Kiểm tra nếu cả hai trường đều trống hoặc có ảnh
            if ((image == null || image.isEmpty()) && (imageUrl == null || imageUrl.trim().isEmpty())) {
                model.addAttribute("errorMessage", "Vui lòng tải lên ảnh hoặc nhập URL.");
                return "house/edit";
            }

            // Kiểm tra nếu cả hai trường đều có giá trị
            if ((image != null && !image.isEmpty()) && (imageUrl != null && !imageUrl.trim().isEmpty())) {
                model.addAttribute("errorMessage", "Chỉ được chọn một trong hai: tải lên ảnh hoặc nhập URL.");
                return "house/edit";
            }

            // Xử lý ảnh nếu có file ảnh
            if (image != null && !image.isEmpty()) {
                byte[] imageBytes = image.getBytes();
                String base64Image = Base64.getEncoder().encodeToString(imageBytes);

                HouseImage houseImage = new HouseImage();
                houseImage.setImageUrl(base64Image);  // Chắc chắn ảnh được chuyển thành Base64
                houseImage.setHouse(house);
                houseImage.setMain(false); // Đánh dấu ảnh này không phải ảnh chính
                house.getHouseImages().add(houseImage);  // Thêm ảnh vào danh sách của house
            }

            // Xử lý ảnh nếu có URL
            if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                if (imageUrl.startsWith("data:")) {
                    // Nếu là data URL, xử lý nó như một chuỗi Base64
                    try {
                        String base64Image = imageUrl.split(",")[1];  // Tách phần Base64 từ data URL
                        byte[] imageBytes = Base64.getDecoder().decode(base64Image);

                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        Thumbnails.of(new ByteArrayInputStream(imageBytes))
                                .size(800, 800)
                                .outputFormat("JPEG")
                                .outputQuality(0.8f)
                                .toOutputStream(outputStream);

                        byte[] resizedImage = outputStream.toByteArray();
                        String resizedBase64Image = Base64.getEncoder().encodeToString(resizedImage);

                        HouseImage houseImage = new HouseImage();
                        houseImage.setImageUrl(resizedBase64Image);  // Lưu ảnh đã được resize vào cơ sở dữ liệu
                        houseImage.setHouse(house);
                        houseImage.setMain(true); // Đánh dấu đây là ảnh chính

                        house.getHouseImages().add(houseImage);
                    } catch (Exception e) {
                        model.addAttribute("errorMessage", "Đã xảy ra lỗi khi xử lý ảnh từ URL.");
                        return "house/edit";
                    }
                } else {
                    // Xử lý ảnh từ URL bình thường
                    try {
                        URL url = new URL(imageUrl);
                        InputStream inputStream = url.openStream();
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                        Thumbnails.of(inputStream)
                                .size(800, 800) // Kích thước mới
                                .outputFormat("JPEG")
                                .outputQuality(0.8f)
                                .toOutputStream(outputStream);

                        byte[] resizedImage = outputStream.toByteArray();
                        String base64Image = Base64.getEncoder().encodeToString(resizedImage);

                        HouseImage houseImage = new HouseImage();
                        houseImage.setImageUrl(base64Image);  // Lưu ảnh vào cơ sở dữ liệu
                        houseImage.setHouse(house);
                        houseImage.setMain(false); // Đánh dấu ảnh này không phải ảnh chính

                        house.getHouseImages().add(houseImage);
                    } catch (IOException e) {
                        model.addAttribute("errorMessage", "Không thể tải ảnh từ URL. Lỗi: " + e.getMessage());
                        return "house/edit";
                    }
                }
            }

            // Cập nhật nhà vào database
            houseService.updateHouse(house, image);

            // Chuyển hướng về trang edit của house sau khi cập nhật
            return "redirect:/house/edit/" + house.getHouseId();

        } catch (Exception e) {
            model.addAttribute("errorMessage", "Đã xảy ra lỗi: " + e.getMessage());
            return "house/edit";
        }
    }




}