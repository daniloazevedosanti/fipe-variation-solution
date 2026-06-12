package br.com.danilo.fipevariation.service;

import br.com.danilo.fipevariation.client.FipeClient;
import br.com.danilo.fipevariation.dto.FipeVehicleDetailResponse;
import br.com.danilo.fipevariation.dto.FipeYearResponse;
import br.com.danilo.fipevariation.dto.VehicleType;
import br.com.danilo.fipevariation.dto.VehicleVariationItemResponse;
import br.com.danilo.fipevariation.dto.VehicleVariationResponse;
import br.com.danilo.fipevariation.exception.BusinessException;
import br.com.danilo.fipevariation.util.MoneyUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
public class VehicleVariationService {

    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    private final FipeClient fipeClient;

    public VehicleVariationService(FipeClient fipeClient) {
        this.fipeClient = fipeClient;
    }

    public VehicleVariationResponse calculateVariation(VehicleType vehicleType,
                                                       Integer brandId,
                                                       Integer modelId,
                                                       Integer reference) {
        List<FipeYearResponse> years = fipeClient.findYears(vehicleType, brandId, modelId, reference)
                .stream()
                .filter(Objects::nonNull)
                .filter(year -> !year.isZeroKm())
                .sorted(Comparator.comparingInt(FipeYearResponse::productionYear))
                .toList();

        if (years.isEmpty()) {
            throw new BusinessException("Nenhum ano de fabricação foi encontrado para a marca/modelo informado.");
        }

        List<DetailWithYearId> detailsAsc = years.stream()
                .map(year -> new DetailWithYearId(year.code(), fipeClient.findDetail(vehicleType, brandId, modelId, year.code(), reference)))
                .filter(item -> item.detail() != null)
                .sorted(Comparator.comparing(item -> item.detail().modelYear()))
                .toList();

        if (detailsAsc.isEmpty()) {
            throw new BusinessException("Não foi possível consultar detalhes de preço para os anos do veículo.");
        }

        List<VehicleVariationItemResponse> itemsAsc = buildVariationItems(detailsAsc);
        List<VehicleVariationItemResponse> itemsDesc = itemsAsc.stream()
                .sorted(Comparator.comparing(VehicleVariationItemResponse::modelYear).reversed())
                .toList();

        return new VehicleVariationResponse(
                vehicleType.apiValue(),
                brandId,
                modelId,
                reference,
                OffsetDateTime.now(),
                itemsDesc
        );
    }

    private List<VehicleVariationItemResponse> buildVariationItems(List<DetailWithYearId> detailsAsc) {
        var result = new java.util.ArrayList<VehicleVariationItemResponse>();

        DetailWithYearId previous = null;
        for (DetailWithYearId current : detailsAsc) {
            BigDecimal currentPrice = MoneyUtils.parseBrazilianCurrency(current.detail().price());

            BigDecimal changeValue = null;
            String changeValueFormatted = null;
            BigDecimal changePercent = null;
            Integer comparedWithYear = null;

            if (previous != null) {
                BigDecimal previousPrice = MoneyUtils.parseBrazilianCurrency(previous.detail().price());
                changeValue = currentPrice.subtract(previousPrice).setScale(2, RoundingMode.HALF_UP);
                changeValueFormatted = MoneyUtils.formatBrazilianCurrency(changeValue);
                changePercent = previousPrice.compareTo(BigDecimal.ZERO) == 0
                        ? BigDecimal.ZERO
                        : changeValue.multiply(ONE_HUNDRED).divide(previousPrice, 2, RoundingMode.HALF_UP);
                comparedWithYear = previous.detail().modelYear();
            }

            result.add(new VehicleVariationItemResponse(
                    current.yearId(),
                    current.detail().modelYear(),
                    current.detail().brand(),
                    current.detail().model(),
                    current.detail().fuel(),
                    current.detail().fuelAcronym(),
                    current.detail().codeFipe(),
                    current.detail().referenceMonth(),
                    current.detail().price(),
                    currentPrice,
                    changeValue,
                    changeValueFormatted,
                    changePercent,
                    comparedWithYear
            ));

            previous = current;
        }

        return result;
    }

    private record DetailWithYearId(String yearId, FipeVehicleDetailResponse detail) {
    }
}
