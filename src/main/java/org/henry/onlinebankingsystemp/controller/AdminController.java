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

    // Service layer dependency to handle admin-related operations.
    private final AdminService adminService;

    /**
     * Endpoint for an admin to suspend a user.
     * @param user_id the ID of the user to be suspended.
     * @return a response indicating the success of the suspension operation.
     */
    @PutMapping("/suspend/{id}")
    private ResponseEntity<DefaultApiResponse<?>> suspendUser(@PathVariable(name = "id") Long user_id) {
        DefaultApiResponse<?> response = new DefaultApiResponse<>();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
