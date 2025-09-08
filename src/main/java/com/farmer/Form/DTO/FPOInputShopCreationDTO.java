package com.farmer.Form.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FPOInputShopCreationDTO {
    @NotBlank(message = "Shop name is required")
    private String shopName;
    private String seedLicense;
    private String pesticideLicense;
    private String fertiliserLicense;
}


