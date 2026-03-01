package com.revworkforce.service;

import com.revworkforce.dto.request.CreateEmployeeRequest;
import com.revworkforce.dto.request.UpdateProfileRequest;
import com.revworkforce.dto.response.UserResponse;
import com.revworkforce.entity.LeaveBalance;
import com.revworkforce.entity.LeaveQuota;
import com.revworkforce.entity.User;
import com.revworkforce.enums.EmployeeStatus;
import com.revworkforce.enums.LeaveType;
import com.revworkforce.enums.Role;
import com.revworkforce.repository.LeaveBalanceRepository;
import com.revworkforce.repository.LeaveQuotaRepository;
import com.revworkforce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final UserRepository userRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final LeaveQuotaRepository leaveQuotaRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse createEmployee(CreateEmployeeRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists: " + request.getEmail());
        }

        User manager = null;
        if (request.getManagerId() != null) {
            manager = userRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new RuntimeException("Manager not found"));
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .address(request.getAddress())
                .department(request.getDepartment())
                .designation(request.getDesignation())
                .employeeCode(request.getEmployeeCode())
                .role(request.getRole())
                .status(EmployeeStatus.ACTIVE)
                .manager(manager)
                .dateOfJoining(request.getDateOfJoining() != null ? request.getDateOfJoining() : LocalDate.now())
                .build();

        user = userRepository.save(user);
        initializeLeaveBalances(user);
        return mapToResponse(user);
    }

    private void initializeLeaveBalances(User user) {
        int currentYear = LocalDate.now().getYear();
        for (LeaveType leaveType : LeaveType.values()) {
            int defaultDays = getDefaultDays(leaveType);
            LeaveBalance balance = LeaveBalance.builder()
                    .employee(user)
                    .leaveType(leaveType)
                    .year(currentYear)
                    .totalDays(defaultDays)
                    .usedDays(0)
                    .build();
            leaveBalanceRepository.save(balance);
        }
    }

    private int getDefaultDays(LeaveType leaveType) {
        return leaveQuotaRepository.findByLeaveType(leaveType)
                .map(LeaveQuota::getDefaultDays)
                .orElseGet(() -> switch (leaveType) {
                    case CASUAL -> 12;
                    case SICK -> 10;
                    case PAID -> 15;
                });
    }

    public UserResponse getEmployee(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        return mapToResponse(user);
    }

    public UserResponse getMyProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToResponse(user);
    }

    public UserResponse updateProfile(String email, UpdateProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getAddress() != null) user.setAddress(request.getAddress());
        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        return mapToResponse(userRepository.save(user));
    }

    public List<UserResponse> getAllEmployees() {
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<UserResponse> getManagers() {
        return userRepository.findByRole(Role.MANAGER).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<UserResponse> getMyTeam(String managerEmail) {
        User manager = userRepository.findByEmail(managerEmail)
                .orElseThrow(() -> new RuntimeException("Manager not found"));
        return userRepository.findByManagerId(manager.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public UserResponse assignManager(Long employeeId, Long managerId) {
        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        User manager = userRepository.findById(managerId)
                .orElseThrow(() -> new RuntimeException("Manager not found"));
        employee.setManager(manager);
        return mapToResponse(userRepository.save(employee));
    }

    public UserResponse toggleStatus(Long employeeId) {
        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        employee.setStatus(employee.getStatus() == EmployeeStatus.ACTIVE
                ? EmployeeStatus.INACTIVE : EmployeeStatus.ACTIVE);
        return mapToResponse(userRepository.save(employee));
    }

    public UserResponse mapToResponse(User user) {
        UserResponse.ManagerInfo managerInfo = null;
        if (user.getManager() != null) {
            managerInfo = UserResponse.ManagerInfo.builder()
                    .id(user.getManager().getId())
                    .firstName(user.getManager().getFirstName())
                    .lastName(user.getManager().getLastName())
                    .email(user.getManager().getEmail())
                    .designation(user.getManager().getDesignation())
                    .build();
        }

        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .address(user.getAddress())
                .department(user.getDepartment())
                .designation(user.getDesignation())
                .employeeCode(user.getEmployeeCode())
                .role(user.getRole())
                .status(user.getStatus())
                .dateOfJoining(user.getDateOfJoining())
                .manager(managerInfo)
                .createdAt(user.getCreatedAt())
                .build();
    }
}
