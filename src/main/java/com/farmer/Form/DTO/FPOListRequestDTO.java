package com.farmer.Form.DTO;

import com.farmer.Form.Entity.FPO;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FPOListRequestDTO {
    
    private String searchTerm;
    private String state;
    private String district;
    private FPO.FPOStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    @Builder.Default
    private Integer page = 0;
    @Builder.Default
    private Integer size = 10;
    @Builder.Default
    private String sortBy = "createdAt";
    @Builder.Default
    private String sortDirection = "desc";
}
