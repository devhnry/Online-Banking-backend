package org.henry.onlinebankingsystemp.controller;

import lombok.RequiredArgsConstructor;
import org.henry.onlinebankingsystemp.dto.DefaultApiResponse;
import org.henry.onlinebankingsystemp.service.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/admin")
public class AdminController {
    public AdminService adminService;

    @PutMapping("/suspend/{id}")
    private ResponseEntity<DefaultApiResponse<?>> suspendUser(@PathVariable(name = "id") Long user_id) {
        DefaultApiResponse<?> response = new DefaultApiResponse<>();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
