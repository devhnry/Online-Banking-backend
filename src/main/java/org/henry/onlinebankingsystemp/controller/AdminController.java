package org.henry.onlinebankingsystemp.controller;

import org.henry.onlinebankingsystemp.dto.RequestResponse;
import org.henry.onlinebankingsystemp.dto.UserInfo;
import org.henry.onlinebankingsystemp.entity.Users;
import org.henry.onlinebankingsystemp.service.AdminService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = {"/admin",})
public class AdminController {
    public AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping(path = "/users")
    private List<UserInfo> getAllUsers() {
        return adminService.getAllUsers();
    }

    @GetMapping(path = "/users/details/{id}")
    private Users getUserDetails(@PathVariable(name = "id") Long user_id) {
        return adminService.getUserInfo(user_id);
    }

    @DeleteMapping(path = "/users/delete/{id}")
    private RequestResponse deleteUser(@PathVariable(name = "id") Long user_id) {
        return adminService.deleteUser(user_id);
    }

    @PutMapping(path = "/users/suspend/{id}")
    private RequestResponse suspendUser(@PathVariable(name = "id") Long user_id) {
        return adminService.suspendUser(user_id);
    }

}
