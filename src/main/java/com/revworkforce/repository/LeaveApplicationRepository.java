package com.revworkforce.repository;

import com.revworkforce.entity.LeaveApplication;
import com.revworkforce.enums.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveApplicationRepository extends JpaRepository<LeaveApplication, Long> {
    List<LeaveApplication> findByEmployeeId(Long employeeId);
    List<LeaveApplication> findByEmployeeIdAndStatus(Long employeeId, LeaveStatus status);

    @Query("SELECT la FROM LeaveApplication la WHERE la.employee.manager.id = ?1")
    List<LeaveApplication> findByManagerId(Long managerId);

    @Query("SELECT la FROM LeaveApplication la WHERE la.employee.manager.id = ?1 AND la.status = ?2")
    List<LeaveApplication> findByManagerIdAndStatus(Long managerId, LeaveStatus status);
}
