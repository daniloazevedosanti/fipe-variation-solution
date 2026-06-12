package br.com.danilo.fipevariation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FipeVehicleDetailResponse(
        String brand,
        String codeFipe,
        String fuel,
        String fuelAcronym,
        String model,
        Integer modelYear,
        String price,
        String referenceMonth,
        Integer vehicleType
) {
}
