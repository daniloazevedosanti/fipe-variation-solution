package br.com.danilo.fipevariation.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record VehicleVariationResponse(
        String vehicleType,
        Integer brandId,
        Integer modelId,
        Integer reference,
        OffsetDateTime generatedAt,
        List<VehicleVariationItemResponse> items
) {
}
