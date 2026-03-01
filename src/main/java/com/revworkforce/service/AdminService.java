package com.revworkforce.service;

import com.revworkforce.dto.request.LeaveQuotaRequest;
import com.revworkforce.dto.response.ApiResponse;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final LeaveQuotaRepository quotaRepository;
    private final LeaveBalanceRepository balanceRepository;

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalEmployees", userRepository.count());
        stats.put("activeEmployees", userRepository.countByStatus(EmployeeStatus.ACTIVE));
        stats.put("inactiveEmployees", userRepository.countByStatus(EmployeeStatus.INACTIVE));
        stats.put("totalManagers", userRepository.countByRole(Role.MANAGER));
        stats.put("totalAdmins", userRepository.countByRole(Role.ADMIN));
        return stats;
    }

    public LeaveQuota configureLeaveQuota(LeaveQuotaRequest request) {
        LeaveQuota quota = quotaRepository.findByLeaveType(request.getLeaveType())
                .orElse(LeaveQuota.builder().leaveType(request.getLeaveType()).build());
        quota.setDefaultDays(request.getDefaultDays());
        quota.setYear(request.getYear() > 0 ? request.getYear() : LocalDate.now().getYear());
        return quotaRepository.save(quota);
    }

    public List<LeaveQuota> getAllQuotas() {
        return quotaRepository.findAll();
    }

    @Transactional
    public void resetLeaveBalancesForNewYear(int year) {
        List<User> allUsers = userRepository.findAll();
        for (User user : allUsers) {
            for (LeaveType leaveType : LeaveType.values()) {
                boolean exists = balanceRepository
                        .findByEmployeeIdAndLeaveTypeAndYear(user.getId(), leaveType, year)
                        .isPresent();
                if (!exists) {
                    int days = quotaRepository.findByLeaveType(leaveType)
                            .map(LeaveQuota::getDefaultDays)
                            .orElseGet(() -> switch (leaveType) {
                                case CASUAL -> 12;
                                case SICK -> 10;
                                case PAID -> 15;
                            });
                    LeaveBalance balance = LeaveBalance.builder()
                            .employee(user)
                            .leaveType(leaveType)
                            .year(year)
                            .totalDays(days)
                            .usedDays(0)
                            .build();
                    balanceRepository.save(balance);
                }
            }
        }
    }
}
