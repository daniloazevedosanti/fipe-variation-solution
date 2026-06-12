package br.com.danilo.fipevariation.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "fipe")
public record FipeClientProperties(
        String baseUrl,
        String subscriptionToken,
        int connectTimeoutMs,
        int readTimeoutMs
) {
}
