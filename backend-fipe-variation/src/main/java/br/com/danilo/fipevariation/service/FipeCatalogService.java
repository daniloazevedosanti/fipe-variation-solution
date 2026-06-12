package br.com.danilo.fipevariation.service;

import br.com.danilo.fipevariation.client.FipeClient;
import br.com.danilo.fipevariation.dto.FipeBrandResponse;
import br.com.danilo.fipevariation.dto.FipeModelResponse;
import br.com.danilo.fipevariation.dto.VehicleType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FipeCatalogService {

    private final FipeClient fipeClient;

    public FipeCatalogService(FipeClient fipeClient) {
        this.fipeClient = fipeClient;
    }

    public List<FipeBrandResponse> findBrands(VehicleType vehicleType, Integer reference) {
        return fipeClient.findBrands(vehicleType, reference);
    }

    public List<FipeModelResponse> findModels(VehicleType vehicleType, Integer brandId, Integer reference) {
        return fipeClient.findModels(vehicleType, brandId, reference);
    }
}
