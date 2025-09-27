package com.farmer.Form.DTO;

import com.farmer.Form.Entity.Country;
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
public class CountryDTO {
    
    private Long id;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<StateDTO> states;
    
    public static CountryDTO fromEntity(Country country) {
        return CountryDTO.builder()
                .id(country.getId())
                .name(country.getName())
                .createdAt(country.getCreatedAt())
                .updatedAt(country.getUpdatedAt())
                .build();
    }
    
    public static CountryDTO fromEntityWithStates(Country country) {
        CountryDTO dto = fromEntity(country);
        if (country.getStates() != null) {
            dto.setStates(country.getStates().stream()
                    .map(StateDTO::fromEntity)
                    .toList());
        }
        return dto;
    }
}
