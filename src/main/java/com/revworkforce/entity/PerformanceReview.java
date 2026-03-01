package com.revworkforce.entity;

import com.revworkforce.enums.ReviewStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "performance_reviews")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PerformanceReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private User employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private User manager;

    private int reviewYear;
    private String reviewPeriod; // e.g. "Q1", "Annual"

    @Column(length = 2000)
    private String selfAssessment;

    @Column(length = 2000)
    private String managerFeedback;

    private Integer rating; // 1-5

    @Enumerated(EnumType.STRING)
    private ReviewStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime submittedAt;
    private LocalDateTime reviewedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        if (status == null) status = ReviewStatus.DRAFT;
    }
}
