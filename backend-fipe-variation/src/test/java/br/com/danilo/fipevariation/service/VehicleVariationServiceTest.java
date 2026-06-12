package br.com.danilo.fipevariation.service;

import br.com.danilo.fipevariation.client.FipeClient;
import br.com.danilo.fipevariation.dto.FipeVehicleDetailResponse;
import br.com.danilo.fipevariation.dto.FipeYearResponse;
import br.com.danilo.fipevariation.dto.VehicleType;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class VehicleVariationServiceTest {

    @Test
    void shouldCalculateVariationByProductionYear() {
        FipeClient client = Mockito.mock(FipeClient.class);
        VehicleVariationService service = new VehicleVariationService(client);

        when(client.findYears(eq(VehicleType.CARS), eq(59), eq(5940), eq(null))).thenReturn(List.of(
                new FipeYearResponse("2013-1", "2013 Gasolina"),
                new FipeYearResponse("2011-1", "2011 Gasolina"),
                new FipeYearResponse("2010-1", "2010 Gasolina"),
                new FipeYearResponse("2009-1", "2009 Gasolina")
        ));

        mockDetail(client, "2009-1", 2009, "R$ 18.225,00");
        mockDetail(client, "2010-1", 2010, "R$ 20.250,00");
        mockDetail(client, "2011-1", 2011, "R$ 22.500,00");
        mockDetail(client, "2013-1", 2013, "R$ 25.000,00");

        var response = service.calculateVariation(VehicleType.CARS, 59, 5940, null);

        assertThat(response.items()).hasSize(4);
        assertThat(response.items().get(0).modelYear()).isEqualTo(2013);
        assertThat(response.items().get(0).comparedWithYear()).isEqualTo(2011);
        assertThat(response.items().get(0).changeValue()).isEqualByComparingTo("2500.00");
        assertThat(response.items().get(0).changePercent()).isEqualByComparingTo("11.11");
        assertThat(response.items().get(3).changeValue()).isNull();
    }

    private static void mockDetail(FipeClient client, String yearId, int year, String price) {
        when(client.findDetail(VehicleType.CARS, 59, 5940, yearId, null)).thenReturn(
                new FipeVehicleDetailResponse(
                        "VW - VolksWagen",
                        "005340-6",
                        "Gasolina",
                        "G",
                        "XPTO",
                        year,
                        price,
                        "junho de 2026",
                        1
                )
        );
    }
}
