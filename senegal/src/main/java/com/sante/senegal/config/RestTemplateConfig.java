package com.sante.senegal.config;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Configuration
public class RestTemplateConfig {

    @Value("${rest.template.connection-timeout:5000}")
    private int connectionTimeout;

    @Value("${rest.template.read-timeout:10000}")
    private int readTimeout;

    @Bean
    public RestTemplate restTemplate() {
        // Configuration des timeouts
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(connectionTimeout, TimeUnit.MILLISECONDS)
                .setResponseTimeout(readTimeout, TimeUnit.MILLISECONDS)
                .build();

        // Création du client HTTP
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(config)
                .build();

        // Configuration de la factory
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);

        // Création et configuration du RestTemplate
        RestTemplate restTemplate = new RestTemplate(factory);
        restTemplate.setInterceptors(Collections.singletonList(new LoggingInterceptor()));

        return restTemplate;
    }
}