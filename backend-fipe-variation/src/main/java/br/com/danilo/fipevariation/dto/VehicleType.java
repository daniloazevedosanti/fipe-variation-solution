package br.com.danilo.fipevariation.dto;

import br.com.danilo.fipevariation.exception.BusinessException;

import java.util.Arrays;

public enum VehicleType {
    CARS("cars", "Carros"),
    MOTORCYCLES("motorcycles", "Motos"),
    TRUCKS("trucks", "Caminhões");

    private final String apiValue;
    private final String description;

    VehicleType(String apiValue, String description) {
        this.apiValue = apiValue;
        this.description = description;
    }

    public String apiValue() {
        return apiValue;
    }

    public String description() {
        return description;
    }

    public static VehicleType fromApiValue(String value) {
        return Arrays.stream(values())
                .filter(type -> type.apiValue.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new BusinessException("Tipo de veículo inválido. Use: cars, motorcycles ou trucks."));
    }
}
