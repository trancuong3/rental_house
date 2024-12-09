package org.example.profilecase5.Controller;

import org.example.profilecase5.Model.RentalNotificationDTO;
import org.example.profilecase5.Service.RentalHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")

public class NotificationController {

    @Autowired
    private RentalHistoryService rentalHistoryService;

    @GetMapping("")
    public ResponseEntity<Page<RentalNotificationDTO>> getNotifications(
            @PageableDefault(size = 10, sort = "startDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<RentalNotificationDTO> notifications = rentalHistoryService.getLatestNotifications(pageable);
        return ResponseEntity.ok(notifications);
    }

}
