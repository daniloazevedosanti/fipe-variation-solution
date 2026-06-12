package br.com.danilo.fipevariation.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class MoneyUtilsTest {

    @Test
    void shouldParseBrazilianCurrency() {
        BigDecimal result = MoneyUtils.parseBrazilianCurrency("R$ 25.000,99");

        assertThat(result).isEqualByComparingTo("25000.99");
    }

    @Test
    void shouldFormatBrazilianCurrency() {
        String result = MoneyUtils.formatBrazilianCurrency(new BigDecimal("2500.00"));

        assertThat(result).contains("2.500,00");
    }
}
