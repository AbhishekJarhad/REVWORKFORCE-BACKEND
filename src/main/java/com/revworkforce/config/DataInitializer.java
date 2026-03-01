package com.revworkforce.config;

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
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final LeaveQuotaRepository leaveQuotaRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        initLeaveQuotas();
        if (!userRepository.existsByEmail("admin@revworkforce.com")) {
            initTestData();
            log.info("=== Test data initialized ===");
            log.info("Admin: admin@revworkforce.com / admin123");
            log.info("Manager: manager@revworkforce.com / manager123");
            log.info("Employee: employee@revworkforce.com / employee123");
        }
    }

    private void initLeaveQuotas() {
        if (leaveQuotaRepository.count() == 0) {
            leaveQuotaRepository.save(LeaveQuota.builder().leaveType(LeaveType.CASUAL).defaultDays(12).year(LocalDate.now().getYear()).build());
            leaveQuotaRepository.save(LeaveQuota.builder().leaveType(LeaveType.SICK).defaultDays(10).year(LocalDate.now().getYear()).build());
            leaveQuotaRepository.save(LeaveQuota.builder().leaveType(LeaveType.PAID).defaultDays(15).year(LocalDate.now().getYear()).build());
        }
    }

    private void initTestData() {
        int year = LocalDate.now().getYear();

        // Create Admin
        User admin = User.builder()
                .email("admin@revworkforce.com")
                .password(passwordEncoder.encode("admin123"))
                .firstName("System")
                .lastName("Admin")
                .employeeCode("EMP001")
                .department("HR")
                .designation("HR Administrator")
                .role(Role.ADMIN)
                .status(EmployeeStatus.ACTIVE)
                .dateOfJoining(LocalDate.of(2020, 1, 1))
                .build();
        admin = userRepository.save(admin);
        createLeaveBalances(admin, year);

        // Create Manager
        User manager = User.builder()
                .email("manager@revworkforce.com")
                .password(passwordEncoder.encode("manager123"))
                .firstName("James")
                .lastName("Wilson")
                .phone("+91-9876543210")
                .employeeCode("EMP002")
                .department("Engineering")
                .designation("Engineering Manager")
                .role(Role.MANAGER)
                .status(EmployeeStatus.ACTIVE)
                .manager(admin)
                .dateOfJoining(LocalDate.of(2021, 3, 15))
                .build();
        manager = userRepository.save(manager);
        createLeaveBalances(manager, year);

        // Create Employee 1
        User emp1 = User.builder()
                .email("employee@revworkforce.com")
                .password(passwordEncoder.encode("employee123"))
                .firstName("Sarah")
                .lastName("Connor")
                .phone("+91-9876543211")
                .address("123 Main St, Pune, Maharashtra")
                .employeeCode("EMP003")
                .department("Engineering")
                .designation("Senior Software Engineer")
                .role(Role.EMPLOYEE)
                .status(EmployeeStatus.ACTIVE)
                .manager(manager)
                .dateOfJoining(LocalDate.of(2022, 6, 1))
                .build();
        emp1 = userRepository.save(emp1);
        createLeaveBalances(emp1, year);

        // Create Employee 2
        User emp2 = User.builder()
                .email("john.doe@revworkforce.com")
                .password(passwordEncoder.encode("employee123"))
                .firstName("John")
                .lastName("Doe")
                .phone("+91-9876543212")
                .address("456 Park Ave, Mumbai, Maharashtra")
                .employeeCode("EMP004")
                .department("Engineering")
                .designation("Software Engineer")
                .role(Role.EMPLOYEE)
                .status(EmployeeStatus.ACTIVE)
                .manager(manager)
                .dateOfJoining(LocalDate.of(2023, 1, 10))
                .build();
        emp2 = userRepository.save(emp2);
        createLeaveBalances(emp2, year);
    }

    private void createLeaveBalances(User user, int year) {
        leaveBalanceRepository.save(LeaveBalance.builder().employee(user).leaveType(LeaveType.CASUAL).year(year).totalDays(12).usedDays(0).build());
        leaveBalanceRepository.save(LeaveBalance.builder().employee(user).leaveType(LeaveType.SICK).year(year).totalDays(10).usedDays(0).build());
        leaveBalanceRepository.save(LeaveBalance.builder().employee(user).leaveType(LeaveType.PAID).year(year).totalDays(15).usedDays(0).build());
    }
}
