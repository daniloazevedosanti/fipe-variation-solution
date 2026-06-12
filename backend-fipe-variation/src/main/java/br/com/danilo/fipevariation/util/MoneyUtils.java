package br.com.danilo.fipevariation.util;

import br.com.danilo.fipevariation.exception.BusinessException;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public final class MoneyUtils {

    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");

    private MoneyUtils() {
    }

    public static BigDecimal parseBrazilianCurrency(String value) {
        if (value == null || value.isBlank()) {
            throw new BusinessException("Preço FIPE ausente ou inválido.");
        }

        String normalized = value
                .replace("R$", "")
                .replace(" ", "")
                .replace(".", "")
                .replace(",", ".")
                .trim();

        try {
            return new BigDecimal(normalized);
        } catch (NumberFormatException ex) {
            throw new BusinessException("Não foi possível converter o preço FIPE: " + value);
        }
    }

    public static String formatBrazilianCurrency(BigDecimal value) {
        if (value == null) {
            return null;
        }
        return NumberFormat.getCurrencyInstance(PT_BR).format(value);
    }
}
