package com.revworkforce.controller;

import com.revworkforce.dto.request.CreateEmployeeRequest;
import com.revworkforce.dto.request.LeaveQuotaRequest;
import com.revworkforce.dto.response.ApiResponse;
import com.revworkforce.dto.response.UserResponse;
import com.revworkforce.entity.LeaveQuota;
import com.revworkforce.service.AdminService;
import com.revworkforce.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final EmployeeService employeeService;
    private final AdminService adminService;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.success(adminService.getDashboardStats()));
    }

    @PostMapping("/employees")
    public ResponseEntity<ApiResponse<UserResponse>> createEmployee(@Valid @RequestBody CreateEmployeeRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Employee created successfully", employeeService.createEmployee(request)));
    }

    @PutMapping("/employees/{id}/assign-manager/{managerId}")
    public ResponseEntity<ApiResponse<UserResponse>> assignManager(@PathVariable Long id, @PathVariable Long managerId) {
        return ResponseEntity.ok(ApiResponse.success("Manager assigned", employeeService.assignManager(id, managerId)));
    }

    @PutMapping("/employees/{id}/toggle-status")
    public ResponseEntity<ApiResponse<UserResponse>> toggleStatus(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Status updated", employeeService.toggleStatus(id)));
    }

    @PostMapping("/leave-quotas")
    public ResponseEntity<ApiResponse<LeaveQuota>> configureLeaveQuota(@RequestBody LeaveQuotaRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Leave quota configured", adminService.configureLeaveQuota(request)));
    }

    @GetMapping("/leave-quotas")
    public ResponseEntity<ApiResponse<List<LeaveQuota>>> getLeaveQuotas() {
        return ResponseEntity.ok(ApiResponse.success(adminService.getAllQuotas()));
    }

    @PostMapping("/reset-leave-balances/{year}")
    public ResponseEntity<ApiResponse<String>> resetLeaveBalances(@PathVariable int year) {
        adminService.resetLeaveBalancesForNewYear(year);
        return ResponseEntity.ok(ApiResponse.success("Leave balances reset for year " + year, null));
    }
}
