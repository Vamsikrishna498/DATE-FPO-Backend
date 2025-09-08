package com.farmer.Form.DTO;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FPOInputShopDTO {
    private Long id;
    private Long fpoId;
    private String fpoName;
    private String shopName;
    private String seedLicense;
    private String pesticideLicense;
    private String fertiliserLicense;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


