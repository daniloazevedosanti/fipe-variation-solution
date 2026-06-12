package br.com.danilo.fipevariation.controller;

import br.com.danilo.fipevariation.dto.FipeBrandResponse;
import br.com.danilo.fipevariation.dto.FipeModelResponse;
import br.com.danilo.fipevariation.dto.VehicleType;
import br.com.danilo.fipevariation.dto.VehicleTypeResponse;
import br.com.danilo.fipevariation.dto.VehicleVariationResponse;
import br.com.danilo.fipevariation.service.FipeCatalogService;
import br.com.danilo.fipevariation.service.VehicleVariationService;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/fipe")
public class FipeController {

    private final FipeCatalogService catalogService;
    private final VehicleVariationService variationService;

    public FipeController(FipeCatalogService catalogService, VehicleVariationService variationService) {
        this.catalogService = catalogService;
        this.variationService = variationService;
    }

    @GetMapping("/vehicle-types")
    public List<VehicleTypeResponse> findVehicleTypes() {
        return Arrays.stream(VehicleType.values())
                .map(VehicleTypeResponse::from)
                .toList();
    }

    @GetMapping("/{vehicleType}/brands")
    public List<FipeBrandResponse> findBrands(@PathVariable String vehicleType,
                                              @RequestParam(required = false) @Min(1) Integer reference) {
        return catalogService.findBrands(VehicleType.fromApiValue(vehicleType), reference);
    }

    @GetMapping("/{vehicleType}/brands/{brandId}/models")
    public List<FipeModelResponse> findModels(@PathVariable String vehicleType,
                                              @PathVariable @Min(1) Integer brandId,
                                              @RequestParam(required = false) @Min(1) Integer reference) {
        return catalogService.findModels(VehicleType.fromApiValue(vehicleType), brandId, reference);
    }

    @GetMapping("/{vehicleType}/brands/{brandId}/models/{modelId}/variations")
    public VehicleVariationResponse calculateVariations(@PathVariable String vehicleType,
                                                        @PathVariable @Min(1) Integer brandId,
                                                        @PathVariable @Min(1) Integer modelId,
                                                        @RequestParam(required = false) @Min(1) Integer reference) {
        return variationService.calculateVariation(VehicleType.fromApiValue(vehicleType), brandId, modelId, reference);
    }
}
