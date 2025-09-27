package com.farmer.Form.DTO;

import com.farmer.Form.Entity.State;
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
public class StateDTO {
    
    private Long id;
    private String name;
    private Long countryId;
    private String countryName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<DistrictDTO> districts;
    
    public static StateDTO fromEntity(State state) {
        return StateDTO.builder()
                .id(state.getId())
                .name(state.getName())
                .countryId(state.getCountry().getId())
                .countryName(state.getCountry().getName())
                .createdAt(state.getCreatedAt())
                .updatedAt(state.getUpdatedAt())
                .build();
    }
    
    public static StateDTO fromEntityWithDistricts(State state) {
        StateDTO dto = fromEntity(state);
        if (state.getDistricts() != null) {
            dto.setDistricts(state.getDistricts().stream()
                    .map(DistrictDTO::fromEntity)
                    .toList());
        }
        return dto;
    }
}
