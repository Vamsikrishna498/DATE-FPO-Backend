package com.farmer.Form.DTO;

import com.farmer.Form.Entity.FPOTurnover;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FPOTurnoverDTO {
    private Long id;
    private Long fpoId;
    private String fpoName;
    private Integer financialYear;
    private Integer month;
    private Integer quarter;
    private Double revenue;
    private Double expenses;
    private Double profit;
    private Double loss;
    private FPOTurnover.TurnoverType turnoverType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String description;
    private String remarks;
    private String documentFileName;
    private String enteredBy;
}
