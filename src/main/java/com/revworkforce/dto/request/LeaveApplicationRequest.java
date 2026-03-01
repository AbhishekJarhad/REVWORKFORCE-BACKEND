package com.revworkforce.dto.request;

import com.revworkforce.enums.LeaveType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class LeaveApplicationRequest {
    @NotNull
    private LeaveType leaveType;
    @NotNull
    private LocalDate startDate;
    @NotNull
    private LocalDate endDate;
    private String reason;
}
