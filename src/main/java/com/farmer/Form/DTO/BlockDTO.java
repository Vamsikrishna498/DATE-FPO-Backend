package com.farmer.Form.DTO;

import com.farmer.Form.Entity.Block;
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
public class BlockDTO {
    
    private Long id;
    private String name;
    private Long districtId;
    private String districtName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<VillageDTO> villages;
    
    public static BlockDTO fromEntity(Block block) {
        return BlockDTO.builder()
                .id(block.getId())
                .name(block.getName())
                .districtId(block.getDistrict().getId())
                .districtName(block.getDistrict().getName())
                .createdAt(block.getCreatedAt())
                .updatedAt(block.getUpdatedAt())
                .build();
    }
    
    public static BlockDTO fromEntityWithVillages(Block block) {
        BlockDTO dto = fromEntity(block);
        if (block.getVillages() != null) {
            dto.setVillages(block.getVillages().stream()
                    .map(VillageDTO::fromEntity)
                    .toList());
        }
        return dto;
    }
}
