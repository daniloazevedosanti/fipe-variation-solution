package br.com.danilo.fipevariation.dto;

public record VehicleTypeResponse(
        String code,
        String description
) {
    public static VehicleTypeResponse from(VehicleType vehicleType) {
        return new VehicleTypeResponse(vehicleType.apiValue(), vehicleType.description());
    }
}
