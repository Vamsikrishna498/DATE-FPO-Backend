package com.farmer.Form.DTO;

import com.farmer.Form.Entity.Village;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VillageDTO {
    
    private Long id;
    private String name;
    private Long blockId;
    private String blockName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ZipcodeDTO> zipcodes;
    
    public static VillageDTO fromEntity(Village village) {
        return VillageDTO.builder()
                .id(village.getId())
                .name(village.getName())
                .blockId(village.getBlock().getId())
                .blockName(village.getBlock().getName())
                .createdAt(village.getCreatedAt())
                .updatedAt(village.getUpdatedAt())
                .build();
    }
    
    public static VillageDTO fromEntityWithZipcodes(Village village) {
        VillageDTO dto = fromEntity(village);
        if (village.getZipcodes() != null) {
            dto.setZipcodes(village.getZipcodes().stream()
                    .map(ZipcodeDTO::fromEntity)
                    .toList());
        }
        return dto;
    }
}
