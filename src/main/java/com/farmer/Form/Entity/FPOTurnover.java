package com.farmer.Form.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "fpo_turnovers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FPOTurnover {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fpo_id", nullable = false)
    private FPO fpo;

    @NotNull(message = "Financial year is required")
    @Column(nullable = false)
    private Integer financialYear;

    @NotNull(message = "Month is required")
    @Min(value = 1, message = "Month must be between 1 and 12")
    @Max(value = 12, message = "Month must be between 1 and 12")
    @Column(nullable = false)
    private Integer month;

    @NotNull(message = "Quarter is required")
    @Min(value = 1, message = "Quarter must be between 1 and 4")
    @Max(value = 4, message = "Quarter must be between 1 and 4")
    @Column(nullable = false)
    private Integer quarter;

    @DecimalMin(value = "0", message = "Revenue must be non-negative")
    @Column(nullable = false)
    private Double revenue;

    @DecimalMin(value = "0", message = "Expenses must be non-negative")
    @Column(nullable = false)
    private Double expenses;

    @DecimalMin(value = "0", message = "Profit must be non-negative")
    private Double profit;

    @DecimalMin(value = "0", message = "Loss must be non-negative")
    private Double loss;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TurnoverType turnoverType;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Additional details
    private String description;
    private String remarks;
    private String documentFileName;
    private String enteredBy;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        calculateProfitLoss();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        calculateProfitLoss();
    }

    private void calculateProfitLoss() {
        if (revenue != null && expenses != null) {
            double difference = revenue - expenses;
            if (difference >= 0) {
                this.profit = difference;
                this.loss = 0.0;
            } else {
                this.profit = 0.0;
                this.loss = Math.abs(difference);
            }
        }
    }

    public enum TurnoverType {
        MONTHLY,
        QUARTERLY,
        YEARLY
    }
}
