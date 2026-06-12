package br.com.danilo.fipevariation.dto;

public record FipeYearResponse(
        String code,
        String name
) {
    public boolean isZeroKm() {
        return code != null && code.startsWith("32000");
    }

    public int productionYear() {
        if (code == null || !code.contains("-")) {
            return 0;
        }
        return Integer.parseInt(code.substring(0, code.indexOf('-')));
    }
}
