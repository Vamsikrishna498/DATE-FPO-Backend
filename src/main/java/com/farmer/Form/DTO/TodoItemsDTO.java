package com.farmer.Form.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodoItemsDTO {
    private Long unassignedFarmers;
    private Long overdueKyc;
    private Long employeesWithHighPending;
} 