package com.revworkforce.dto.request;

import lombok.Data;

@Data
public class PerformanceReviewRequest {
    private int reviewYear;
    private String reviewPeriod;
    private String selfAssessment;
}
