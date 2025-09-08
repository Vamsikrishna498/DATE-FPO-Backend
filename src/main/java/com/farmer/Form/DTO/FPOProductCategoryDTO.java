package com.farmer.Form.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FPOProductCategoryDTO {
    private Long id;
    private Long fpoId;
    private String fpoName;
    private String categoryName;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long productCount;
}
