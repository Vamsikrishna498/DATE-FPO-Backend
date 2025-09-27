package com.farmer.Form.DTO;

import com.farmer.Form.Entity.District;
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
public class DistrictDTO {
    
    private Long id;
    private String name;
    private Long stateId;
    private String stateName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<BlockDTO> blocks;
    
    public static DistrictDTO fromEntity(District district) {
        return DistrictDTO.builder()
                .id(district.getId())
                .name(district.getName())
                .stateId(district.getState().getId())
                .stateName(district.getState().getName())
                .createdAt(district.getCreatedAt())
                .updatedAt(district.getUpdatedAt())
                .build();
    }
    
    public static DistrictDTO fromEntityWithBlocks(District district) {
        DistrictDTO dto = fromEntity(district);
        if (district.getBlocks() != null) {
            dto.setBlocks(district.getBlocks().stream()
                    .map(BlockDTO::fromEntity)
                    .toList());
        }
        return dto;
    }
}
