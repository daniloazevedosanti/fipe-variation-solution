package br.com.danilo.fipevariation.client;

import br.com.danilo.fipevariation.dto.FipeBrandResponse;
import br.com.danilo.fipevariation.dto.FipeModelResponse;
import br.com.danilo.fipevariation.dto.FipeVehicleDetailResponse;
import br.com.danilo.fipevariation.dto.FipeYearResponse;
import br.com.danilo.fipevariation.dto.VehicleType;
import br.com.danilo.fipevariation.exception.ExternalApiException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.util.List;
import java.util.function.Function;

@Component
public class FipeClient {

    private final RestClient restClient;

    public FipeClient(RestClient fipeRestClient) {
        this.restClient = fipeRestClient;
    }

    @Cacheable(cacheNames = "brands", key = "#vehicleType.apiValue() + ':' + #reference")
    public List<FipeBrandResponse> findBrands(VehicleType vehicleType, Integer reference) {
        return getList(uriBuilder -> withReference(
                uriBuilder.path("/{vehicleType}/brands"), reference)
                .build(vehicleType.apiValue()),
                new ParameterizedTypeReference<List<FipeBrandResponse>>() {});
    }

    @Cacheable(cacheNames = "models", key = "#vehicleType.apiValue() + ':' + #brandId + ':' + #reference")
    public List<FipeModelResponse> findModels(VehicleType vehicleType, Integer brandId, Integer reference) {
        return getList(uriBuilder -> withReference(
                uriBuilder.path("/{vehicleType}/brands/{brandId}/models"), reference)
                .build(vehicleType.apiValue(), brandId),
                new ParameterizedTypeReference<List<FipeModelResponse>>() {});
    }

    @Cacheable(cacheNames = "years", key = "#vehicleType.apiValue() + ':' + #brandId + ':' + #modelId + ':' + #reference")
    public List<FipeYearResponse> findYears(VehicleType vehicleType, Integer brandId, Integer modelId, Integer reference) {
        return getList(uriBuilder -> withReference(
                uriBuilder.path("/{vehicleType}/brands/{brandId}/models/{modelId}/years"), reference)
                .build(vehicleType.apiValue(), brandId, modelId),
                new ParameterizedTypeReference<List<FipeYearResponse>>() {});
    }

    @Cacheable(cacheNames = "vehicle-details", key = "#vehicleType.apiValue() + ':' + #brandId + ':' + #modelId + ':' + #yearId + ':' + #reference")
    public FipeVehicleDetailResponse findDetail(VehicleType vehicleType,
                                                Integer brandId,
                                                Integer modelId,
                                                String yearId,
                                                Integer reference) {
        try {
            return restClient.get()
                    .uri(uriBuilder -> withReference(
                            uriBuilder.path("/{vehicleType}/brands/{brandId}/models/{modelId}/years/{yearId}"), reference)
                            .build(vehicleType.apiValue(), brandId, modelId, yearId))
                    .retrieve()
                    .body(FipeVehicleDetailResponse.class);
        } catch (RestClientException ex) {
            throw new ExternalApiException("Erro ao consultar detalhe do veículo na FIPE.", ex);
        }
    }

    private <T> List<T> getList(Function<UriBuilder, URI> uriFunction, ParameterizedTypeReference<List<T>> responseType) {
        try {
            return restClient.get()
                    .uri(uriFunction)
                    .retrieve()
                    .body(responseType);
        } catch (RestClientException ex) {
            throw new ExternalApiException("Erro ao consultar dados na API FIPE.", ex);
        }
    }

    private UriBuilder withReference(UriBuilder builder, Integer reference) {
        if (reference != null) {
            builder.queryParam("reference", reference);
        }
        return builder;
    }
}
