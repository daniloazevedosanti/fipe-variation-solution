package br.com.danilo.fipevariation.dto;

import java.math.BigDecimal;

public record VehicleVariationItemResponse(
        String yearId,
        Integer modelYear,
        String brand,
        String model,
        String fuel,
        String fuelAcronym,
        String codeFipe,
        String referenceMonth,
        String price,
        BigDecimal priceValue,
        BigDecimal changeValue,
        String changeValueFormatted,
        BigDecimal changePercent,
        Integer comparedWithYear
) {
}
