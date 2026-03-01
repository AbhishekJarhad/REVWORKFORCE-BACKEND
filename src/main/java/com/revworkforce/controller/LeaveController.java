package com.revworkforce.controller;

import com.revworkforce.dto.request.LeaveActionRequest;
import com.revworkforce.dto.request.LeaveApplicationRequest;
import com.revworkforce.dto.response.ApiResponse;
import com.revworkforce.dto.response.LeaveApplicationResponse;
import com.revworkforce.dto.response.LeaveBalanceResponse;
import com.revworkforce.service.LeaveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveService leaveService;

    @GetMapping("/balance")
    public ResponseEntity<ApiResponse<List<LeaveBalanceResponse>>> getBalance(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(leaveService.getMyLeaveBalance(userDetails.getUsername())));
    }

    @PostMapping("/apply")
    public ResponseEntity<ApiResponse<LeaveApplicationResponse>> applyLeave(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody LeaveApplicationRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Leave applied", leaveService.applyLeave(userDetails.getUsername(), request)));
    }

    @GetMapping("/my-leaves")
    public ResponseEntity<ApiResponse<List<LeaveApplicationResponse>>> getMyLeaves(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(leaveService.getMyLeaves(userDetails.getUsername())));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<LeaveApplicationResponse>> cancelLeave(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Leave cancelled", leaveService.cancelLeave(userDetails.getUsername(), id)));
    }

    @GetMapping("/team")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<LeaveApplicationResponse>>> getTeamLeaves(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(leaveService.getTeamLeaves(userDetails.getUsername())));
    }

    @GetMapping("/team/pending")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<LeaveApplicationResponse>>> getPendingTeamLeaves(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(leaveService.getPendingTeamLeaves(userDetails.getUsername())));
    }

    @PutMapping("/{id}/process")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<LeaveApplicationResponse>> processLeave(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @RequestBody LeaveActionRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Leave processed", leaveService.processLeave(userDetails.getUsername(), id, request)));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<LeaveApplicationResponse>>> getAllLeaves() {
        return ResponseEntity.ok(ApiResponse.success(leaveService.getAllLeaves()));
    }
}
