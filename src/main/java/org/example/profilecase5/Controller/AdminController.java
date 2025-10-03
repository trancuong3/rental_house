package org.example.profilecase5.Controller;

import org.example.profilecase5.Exception.User.EmailAlreadyExistsException;
import org.example.profilecase5.Exception.User.PasswordValidationException;
import org.example.profilecase5.Exception.User.PhoneAlreadyExistsException;
import org.example.profilecase5.Exception.User.UsernameAlreadyExistsException;
import org.example.profilecase5.Model.*;
import org.example.profilecase5.Service.EmailService;
import org.example.profilecase5.Service.HouseService;
import org.example.profilecase5.Service.UserService;
import org.example.profilecase5.Service.WaitingOwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;
    @Autowired
    private HouseService houseService;
    @Autowired
    private WaitingOwnerService waitingOwnerService;
    @Autowired
    private EmailService emailService;

    // ---------------- DANH SÁCH USER ----------------
    @GetMapping("")
    public String getUsers(Model model) {
        List<User> user = userService.getAllUsers();
        model.addAttribute("user", user);
        return "admin/admin"; // Trang danh sách user
    }

    // ---------------- BẬT / TẮT TRẠNG THÁI USER ----------------
    @PostMapping("/toggleStatus/{userId}")
    public String toggleStatus(@PathVariable int userId) {
        userService.toggleUserStatus(userId);
        return "redirect:/admin";
    }

    // ---------------- CHI TIẾT USER ----------------
    @GetMapping("/detail/{userId}")
    public String userDetails(@PathVariable int userId, Model model) {
        User user = userService.getUserById(userId);
        if (user == null) {
            throw new RuntimeException("User not found with id: " + userId);
        }

        // Lấy lịch sử thuê nhà
        Set<RentalHistory> rentalHistories = user.getRentalHistories();
        model.addAttribute("user", user);
        model.addAttribute("rentalHistories", rentalHistories);

        // Tính tổng chi tiêu
        double totalSpent = rentalHistories.stream().mapToDouble(RentalHistory::getTotalCost).sum();
        model.addAttribute("totalSpent", totalSpent);

        return "admin/userDetail";
    }

    // ---------------- QUẢN LÝ HOUSE ----------------
    @GetMapping("/house")
    public String house(Model model,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<House> housePage = houseService.getHouses(pageable);
        List<House> topHouses = houseService.getTop5MostRentedHouses();

        model.addAttribute("housePage", housePage);
        model.addAttribute("topHouses", topHouses);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", housePage.getTotalPages());

        return "admin/house";
    }

    // ---------------- QUẢN LÝ CHỜ DUYỆT CHỦ NHÀ ----------------
    @GetMapping("/waiting-owners")
    public String showWaitingOwners(Model model) {
        List<WaitingOwner> waitingOwners = waitingOwnerService.getAllWaitingOwners();
        model.addAttribute("waitingOwners", waitingOwners);
        return "admin/waiting-owners";
    }

    // Chấp nhận chủ nhà
    @PostMapping("/waiting-owners/accept/{id}")
    public String acceptOwner(@PathVariable("id") int id) {
        WaitingOwner waitingOwner = waitingOwnerService.findById(id);
        if (waitingOwner == null) {
            throw new RuntimeException("WaitingOwner not found with id: " + id);
        }
        String email = waitingOwner.getEmail();

        waitingOwnerService.acceptWaitingOwner(id);

        emailService.sendEmail(
                email,
                "Đăng ký làm chủ nhà được chấp nhận",
                "Xin chúc mừng, đăng ký làm chủ nhà của bạn đã được chấp nhận!\n" +
                        "Hãy đăng nhập để sử dụng dịch vụ: http://localhost:8080/login"
        );

        return "redirect:/admin/waiting-owners";
    }

    // Từ chối chủ nhà
    @PostMapping("/waiting-owners/refuse/{id}")
    public String refuseOwner(@PathVariable("id") int id) {
        WaitingOwner waitingOwner = waitingOwnerService.findById(id);
        waitingOwnerService.refuseWaitingOwner(id);

        String email = waitingOwner.getEmail();
        emailService.sendEmail(
                email,
                "Đăng ký làm chủ nhà bị từ chối",
                "Rất tiếc, đơn đăng ký làm chủ nhà của bạn đã bị từ chối."
        );

        return "redirect:/admin/waiting-owners";
    }

    // ---------------- DANH SÁCH OWNER ----------------
    @GetMapping("/owners")
    public String listOwners(Model model) {
        List<User> owners = userService.getAllOwners(); // roleId = 3
        model.addAttribute("owners", owners);
        return "admin/owner-list";
    }

    // ---------------- THÊM OWNER ----------------
    @GetMapping("/owner/add")
    public String showAddOwnerForm(Model model) {
        User owner = new User();
        model.addAttribute("owner", owner);
        return "admin/owner-add-form"; // Tách riêng form Add
    }

    @PostMapping("/owner/add")
    public String addOwner(@ModelAttribute("owner") User owner,
                           RedirectAttributes redirectAttributes) {
        try {
            String randomPassword = UUID.randomUUID().toString().replace("-", "").substring(0, 8);

            owner.setPassword(randomPassword);
            owner.setConfirmPassword(randomPassword);

            // 🔹 Cố định role = 3 (Owner) trước khi register
            owner.setRole(userService.getRoleById(3));
            owner.setStatus(User.Status.Active);

            // 🔹 Đăng ký Owner
            userService.registerOwner(owner);

            // 🔹 Encode mật khẩu và cập nhật lại
            String encodedPassword = userService.encodePassword(randomPassword);
            owner.setPassword(encodedPassword);
            owner.setConfirmPassword(encodedPassword);
            userService.updateUser(owner);

            // 🔹 Gửi email
            emailService.sendEmail(owner.getEmail(),
                    "Tài khoản Owner mới được tạo",
                    "Xin chào " + owner.getFullname() + ",\n" +
                            "Tên đăng nhập: " + owner.getUsername() + "\n" +
                            "Mật khẩu: " + randomPassword);

        } catch (UsernameAlreadyExistsException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Tên người dùng đã tồn tại.");
            return "redirect:/admin/owner/add";
        } catch (EmailAlreadyExistsException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Email đã tồn tại.");
            return "redirect:/admin/owner/add";
        } catch (PhoneAlreadyExistsException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Số điện thoại đã tồn tại.");
            return "redirect:/admin/owner/add";
        } catch (PasswordValidationException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mật khẩu không hợp lệ!");
            return "redirect:/admin/owner/add";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi thêm Owner!");
            return "redirect:/admin/owner/add";
        }

        redirectAttributes.addFlashAttribute("successMessage", "Thêm Owner thành công! Mật khẩu đã gửi qua email.");
        return "redirect:/admin/owners";
    }


    // ---------------- SỬA OWNER ----------------
    @GetMapping("/owner/edit/{id}")
    public String showEditOwnerForm(@PathVariable int id, Model model) {
        User owner = userService.getUserById(id);
        if (owner == null) throw new RuntimeException("Owner không tồn tại");
        model.addAttribute("owner", owner);
        return "admin/owner-edit-form"; // Tách riêng form Edit
    }

    @PostMapping("/owner/edit/{id}")
    public String updateOwner(@PathVariable int id,
                              @ModelAttribute User updatedOwner,
                              RedirectAttributes redirectAttributes) {
        User existing = userService.getUserById(id);
        if (existing == null) throw new RuntimeException("Owner không tồn tại");

        existing.setFullname(updatedOwner.getFullname());
        existing.setEmail(updatedOwner.getEmail());
        existing.setPhone(updatedOwner.getPhone());
        existing.setStatus(updatedOwner.getStatus());

        // Cố định role = 3
        existing.setRole(userService.getRoleById(3));

        userService.updateUser(existing);

        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật Owner thành công!");
        return "redirect:/admin/owners";
    }

    // ---------------- CHI TIẾT OWNER ----------------
    @GetMapping("/owner/detail/{id}")
    public String ownerDetails(@PathVariable int id, Model model) {
        User owner = userService.getUserById(id);
        if (owner == null) throw new RuntimeException("Owner không tồn tại");

        Set<RentalHistory> rentalHistories = owner.getRentalHistories();
        model.addAttribute("owner", owner);
        model.addAttribute("rentalHistories", rentalHistories);

        double totalSpent = rentalHistories.stream().mapToDouble(RentalHistory::getTotalCost).sum();
        model.addAttribute("totalSpent", totalSpent);

        return "admin/ownerDetail";
    }







    // ---------------- SỬA USER (KHÔNG SỬA MẬT KHẨU) ----------------
    @GetMapping("/user/edit/{id}")
    public String showEditUserForm(@PathVariable("id") int id, Model model) {
        User user = userService.getUserById(id);
        if (user == null) {
            throw new RuntimeException("Không tìm thấy user với id = " + id);
        }

        List<Role> roles = userService.getAllRoles(); // <-- lấy tất cả roles
        model.addAttribute("user", user);
        model.addAttribute("roles", roles);

        return "admin/user-edit-form";
    }




    @PostMapping("/user/edit/{id}")
    public String updateUser(@PathVariable("id") int id,
                             @ModelAttribute User updatedUser) {
        User existing = userService.getUserById(id);
        if (existing == null) throw new RuntimeException("User not found");

        existing.setFullname(updatedUser.getFullname());
        existing.setEmail(updatedUser.getEmail());
        existing.setPhone(updatedUser.getPhone());
        existing.setStatus(updatedUser.getStatus());

        // Lấy role từ DB bằng roleId
        int roleId = updatedUser.getRole().getRoleId();
        Role role = userService.getRoleById(roleId);
        existing.setRole(role);

        userService.updateUser(existing);
        return "redirect:/admin";
    }


    // ---------------- RESET MẬT KHẨU ----------------
    @PostMapping("/user/reset-password/{id}")
    public String resetPassword(@PathVariable("id") int id) {
        User user = userService.getUserById(id);
        if (user == null) throw new RuntimeException("User not found");

        // random mật khẩu mới
        String newPassword = UUID.randomUUID().toString().substring(0, 8);
        String encoded = userService.encodePassword(newPassword);

        user.setPassword(encoded);
        user.setConfirmPassword(encoded);
        userService.updateUser(user);

        // gửi email mật khẩu mới
        emailService.sendEmail(user.getEmail(),
                "Reset mật khẩu",
                "Mật khẩu mới của bạn là: " + newPassword);

        return "redirect:/admin/detail/" + id;
    }
    // ---------------- THÊM USER ----------------
// Hiển thị form thêm user
    // Hiển thị form thêm user
    @GetMapping("/user/add")
    public String showAddUserForm(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "admin/user-form"; // dùng chung với Add
    }

    // Xử lý submit form Add User (bỏ validate)
    // Xử lý submit form Add User (bỏ validate)
    @PostMapping("/user/add")
    public String addUser(@ModelAttribute("user") User user,
                          RedirectAttributes redirectAttributes) {

        try {
            // 🔹 Tạo mật khẩu ngẫu nhiên hợp lệ 8 ký tự (chỉ chữ và số)
            String randomPassword = UUID.randomUUID().toString().replace("-", "").substring(0, 8);

            // 🔹 Set mật khẩu gốc để validate trong registerUser()
            user.setPassword(randomPassword);
            user.setConfirmPassword(randomPassword);

            // 🔹 Role mặc định USER (2), Status mặc định Active
            user.setRole(userService.getRoleById(2));
            user.setStatus(User.Status.Active);

            // 🔹 Đăng ký user (validation mật khẩu gốc)
            userService.registerUser(user);

            // 🔹 Encode mật khẩu và lưu vào DB
            String encodedPassword = userService.encodePassword(randomPassword);
            user.setPassword(encodedPassword);
            user.setConfirmPassword(encodedPassword);
            userService.updateUser(user);

            // 🔹 Gửi email mật khẩu
            emailService.sendEmail(user.getEmail(),
                    "Tài khoản mới được tạo",
                    "Xin chào " + user.getFullname() + ",\n" +
                            "Tên đăng nhập: " + user.getUsername() + "\n" +
                            "Mật khẩu: " + randomPassword);

        } catch (UsernameAlreadyExistsException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Tên người dùng đã tồn tại.");
            return "redirect:/admin/user/add";
        } catch (EmailAlreadyExistsException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Email đã tồn tại.");
            return "redirect:/admin/user/add";
        } catch (PhoneAlreadyExistsException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Số điện thoại đã tồn tại.");
            return "redirect:/admin/user/add";
        } catch (PasswordValidationException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mật khẩu không hợp lệ!");
            return "redirect:/admin/user/add";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi thêm user!");
            return "redirect:/admin/user/add";
        }

        redirectAttributes.addFlashAttribute("successMessage",
                "Thêm user thành công! Mật khẩu đã gửi qua email.");
        return "redirect:/admin";
    }





}
