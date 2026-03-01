package com.revworkforce.service;

import com.revworkforce.dto.request.LeaveActionRequest;
import com.revworkforce.dto.request.LeaveApplicationRequest;
import com.revworkforce.dto.response.LeaveApplicationResponse;
import com.revworkforce.dto.response.LeaveBalanceResponse;
import com.revworkforce.entity.LeaveApplication;
import com.revworkforce.entity.LeaveBalance;
import com.revworkforce.entity.User;
import com.revworkforce.enums.LeaveStatus;
import com.revworkforce.repository.LeaveApplicationRepository;
import com.revworkforce.repository.LeaveBalanceRepository;
import com.revworkforce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaveService {

    private final LeaveApplicationRepository leaveRepo;
    private final LeaveBalanceRepository balanceRepo;
    private final UserRepository userRepository;

    public List<LeaveBalanceResponse> getMyLeaveBalance(String email) {
        User user = getUser(email);
        int year = LocalDate.now().getYear();
        return balanceRepo.findByEmployeeIdAndYear(user.getId(), year).stream()
                .map(this::mapBalance)
                .collect(Collectors.toList());
    }

    @Transactional
    public LeaveApplicationResponse applyLeave(String email, LeaveApplicationRequest request) {
        User user = getUser(email);

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new RuntimeException("End date cannot be before start date");
        }

        int days = (int) ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1;
        int year = request.getStartDate().getYear();

        LeaveBalance balance = balanceRepo.findByEmployeeIdAndLeaveTypeAndYear(user.getId(), request.getLeaveType(), year)
                .orElseThrow(() -> new RuntimeException("Leave balance not found"));

        if (balance.getRemainingDays() < days) {
            throw new RuntimeException("Insufficient leave balance. Available: " + balance.getRemainingDays() + " days");
        }

        LeaveApplication application = LeaveApplication.builder()
                .employee(user)
                .leaveType(request.getLeaveType())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .numberOfDays(days)
                .reason(request.getReason())
                .status(LeaveStatus.PENDING)
                .build();

        return mapApplication(leaveRepo.save(application));
    }

    public List<LeaveApplicationResponse> getMyLeaves(String email) {
        User user = getUser(email);
        return leaveRepo.findByEmployeeId(user.getId()).stream()
                .map(this::mapApplication)
                .collect(Collectors.toList());
    }

    @Transactional
    public LeaveApplicationResponse cancelLeave(String email, Long leaveId) {
        User user = getUser(email);
        LeaveApplication application = leaveRepo.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Leave not found"));

        if (!application.getEmployee().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        if (application.getStatus() != LeaveStatus.PENDING) {
            throw new RuntimeException("Only pending leaves can be cancelled");
        }

        application.setStatus(LeaveStatus.CANCELLED);
        return mapApplication(leaveRepo.save(application));
    }

    public List<LeaveApplicationResponse> getTeamLeaves(String managerEmail) {
        User manager = getUser(managerEmail);
        return leaveRepo.findByManagerId(manager.getId()).stream()
                .map(this::mapApplication)
                .collect(Collectors.toList());
    }

    public List<LeaveApplicationResponse> getPendingTeamLeaves(String managerEmail) {
        User manager = getUser(managerEmail);
        return leaveRepo.findByManagerIdAndStatus(manager.getId(), LeaveStatus.PENDING).stream()
                .map(this::mapApplication)
                .collect(Collectors.toList());
    }

    @Transactional
    public LeaveApplicationResponse processLeave(String managerEmail, Long leaveId, LeaveActionRequest request) {
        User manager = getUser(managerEmail);
        LeaveApplication application = leaveRepo.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Leave not found"));

        if (!application.getEmployee().getManager().getId().equals(manager.getId())) {
            throw new RuntimeException("Unauthorized to process this leave");
        }
        if (application.getStatus() != LeaveStatus.PENDING) {
            throw new RuntimeException("Leave is not in pending status");
        }

        if ("APPROVE".equalsIgnoreCase(request.getAction())) {
            application.setStatus(LeaveStatus.APPROVED);
            application.setApprovedBy(manager);
            // Deduct from balance
            LeaveBalance balance = balanceRepo.findByEmployeeIdAndLeaveTypeAndYear(
                    application.getEmployee().getId(), application.getLeaveType(),
                    application.getStartDate().getYear())
                    .orElseThrow(() -> new RuntimeException("Balance not found"));
            balance.setUsedDays(balance.getUsedDays() + application.getNumberOfDays());
            balanceRepo.save(balance);
        } else if ("REJECT".equalsIgnoreCase(request.getAction())) {
            application.setStatus(LeaveStatus.REJECTED);
            application.setApprovedBy(manager);
        } else {
            throw new RuntimeException("Invalid action: " + request.getAction());
        }

        application.setManagerComment(request.getComment());
        return mapApplication(leaveRepo.save(application));
    }

    public List<LeaveApplicationResponse> getAllLeaves() {
        return leaveRepo.findAll().stream()
                .map(this::mapApplication)
                .collect(Collectors.toList());
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private LeaveBalanceResponse mapBalance(LeaveBalance b) {
        return LeaveBalanceResponse.builder()
                .id(b.getId())
                .leaveType(b.getLeaveType())
                .year(b.getYear())
                .totalDays(b.getTotalDays())
                .usedDays(b.getUsedDays())
                .remainingDays(b.getRemainingDays())
                .build();
    }

    private LeaveApplicationResponse mapApplication(LeaveApplication a) {
        return LeaveApplicationResponse.builder()
                .id(a.getId())
                .employeeName(a.getEmployee().getFullName())
                .employeeCode(a.getEmployee().getEmployeeCode())
                .department(a.getEmployee().getDepartment())
                .leaveType(a.getLeaveType())
                .startDate(a.getStartDate())
                .endDate(a.getEndDate())
                .numberOfDays(a.getNumberOfDays())
                .reason(a.getReason())
                .managerComment(a.getManagerComment())
                .status(a.getStatus())
                .approvedByName(a.getApprovedBy() != null ? a.getApprovedBy().getFullName() : null)
                .appliedAt(a.getAppliedAt())
                .updatedAt(a.getUpdatedAt())
                .build();
    }
}
