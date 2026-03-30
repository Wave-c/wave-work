package com.wave.notification_service.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NextRecommendedWorkResponse {
    private List<WorkDto> items;
    private String nextCursor;
    private Boolean hasMore;
}
