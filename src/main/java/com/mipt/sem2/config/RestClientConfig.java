package com.mipt.sem2.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class RestClientConfig {

    @Value("${app.external.base-url}")
    private String baseUrl;

    @Value("${app.external.connect-timeout-ms}")
    private int connectTimeoutMs;

    @Value("${app.external.read-timeout-ms}")
    private int readTimeoutMs;

    @Bean
    public RestClient externalRestClient() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofMillis(connectTimeoutMs));
        factory.setReadTimeout(Duration.ofMillis(readTimeoutMs));

        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .defaultHeader(HttpHeaders.USER_AGENT, "todo-gateway/1.0")
                .build();
    }
}
