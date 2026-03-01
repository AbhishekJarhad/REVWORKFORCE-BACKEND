package com.revworkforce.dto.request;

import lombok.Data;

@Data
public class LeaveActionRequest {
    private String action; // APPROVE or REJECT
    private String comment;
}
