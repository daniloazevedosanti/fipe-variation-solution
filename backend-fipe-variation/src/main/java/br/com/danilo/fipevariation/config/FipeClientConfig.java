package br.com.danilo.fipevariation.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
@EnableConfigurationProperties(FipeClientProperties.class)
public class FipeClientConfig {

    @Bean
    RestClient fipeRestClient(FipeClientProperties properties, RestTemplateBuilder builder) {
        var restTemplate = builder
                .setConnectTimeout(Duration.ofMillis(properties.connectTimeoutMs()))
                .setReadTimeout(Duration.ofMillis(properties.readTimeoutMs()))
                .build();

        return RestClient.builder(restTemplate)
                .baseUrl(properties.baseUrl())
                .defaultHeader(HttpHeaders.ACCEPT, "application/json")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .requestInterceptor((request, body, execution) -> {
                    if (StringUtils.hasText(properties.subscriptionToken())) {
                        request.getHeaders().set("X-Subscription-Token", properties.subscriptionToken());
                    }
                    return execution.execute(request, body);
                })
                .build();
    }
}
