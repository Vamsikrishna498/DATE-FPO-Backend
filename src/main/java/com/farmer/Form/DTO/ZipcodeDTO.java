package com.farmer.Form.DTO;

import com.farmer.Form.Entity.Zipcode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZipcodeDTO {
    
    private Long id;
    private String code;
    private Long villageId;
    private String villageName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static ZipcodeDTO fromEntity(Zipcode zipcode) {
        return ZipcodeDTO.builder()
                .id(zipcode.getId())
                .code(zipcode.getCode())
                .villageId(zipcode.getVillage().getId())
                .villageName(zipcode.getVillage().getName())
                .createdAt(zipcode.getCreatedAt())
                .updatedAt(zipcode.getUpdatedAt())
                .build();
    }
}
