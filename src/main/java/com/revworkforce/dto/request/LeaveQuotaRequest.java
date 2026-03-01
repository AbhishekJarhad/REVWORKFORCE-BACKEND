package com.revworkforce.dto.request;

import com.revworkforce.enums.LeaveType;
import lombok.Data;

@Data
public class LeaveQuotaRequest {
    private LeaveType leaveType;
    private int defaultDays;
    private int year;
}
