package com.farmer.Form.DTO;

import com.farmer.Form.Entity.FPOTurnover;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FPOTurnoverCreationDTO {
    
    @NotNull(message = "Financial year is required")
    private Integer financialYear;

    @NotNull(message = "Month is required")
    @Min(value = 1, message = "Month must be between 1 and 12")
    @Max(value = 12, message = "Month must be between 1 and 12")
    private Integer month;

    @NotNull(message = "Quarter is required")
    @Min(value = 1, message = "Quarter must be between 1 and 4")
    @Max(value = 4, message = "Quarter must be between 1 and 4")
    private Integer quarter;

    @DecimalMin(value = "0", message = "Revenue must be non-negative")
    @NotNull(message = "Revenue is required")
    private Double revenue;

    @DecimalMin(value = "0", message = "Expenses must be non-negative")
    @NotNull(message = "Expenses is required")
    private Double expenses;

    @NotNull(message = "Turnover type is required")
    private FPOTurnover.TurnoverType turnoverType;

    private String description;
    private String remarks;
    private String documentFileName;
    private String enteredBy;
}
