package com.sante.senegal.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class LoggingInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {

        long startTime = System.currentTimeMillis();
        log.debug("Appel API: {} {}", request.getMethod(), request.getURI());

        ClientHttpResponse response = execution.execute(request, body);

        long endTime = System.currentTimeMillis();
        log.debug("Réponse API: {} - {} ms", response.getStatusCode(), (endTime - startTime));

        return response;
    }
}
