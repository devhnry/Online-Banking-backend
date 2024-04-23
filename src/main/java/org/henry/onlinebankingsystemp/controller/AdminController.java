package org.henry.onlinebankingsystemp.controller;

import lombok.RequiredArgsConstructor;
import org.henry.onlinebankingsystemp.dto.DefaultResponse;
import org.henry.onlinebankingsystemp.service.AdminService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/admin")
public class AdminController {
    public AdminService adminService;

    @PutMapping("/suspend/{id}")
    private DefaultResponse suspendUser(@PathVariable(name = "id") Long user_id) {
        return adminService.suspendUser(user_id);
    }

}
