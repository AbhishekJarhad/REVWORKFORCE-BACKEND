package com.revworkforce.dto.request;

import com.revworkforce.enums.GoalStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class GoalRequest {
    private String title;
    private String description;
    private int year;
    private LocalDate targetDate;
    private int progressPercentage;
    private GoalStatus status;
}
