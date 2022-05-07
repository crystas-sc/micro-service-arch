package com.capps.ocr.service;

import com.capps.ocr.exceptions.NERServiceNotFoundException;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NerServiceRequesterImpl implements NerService {
    @Value("${spring.application.nerServiceUrl}")
    String nerServiceUrl;

    @Override
    @Retry(name = "nerService", fallbackMethod = "getNERFallback")
    @CircuitBreaker(name = "nerServiceCircuitBreaker" , fallbackMethod = "getNERFallback")
    public String getNER(String text) {
        log.debug("Sending request to NER proxy service");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject reqJsonObject = new JSONObject();
        reqJsonObject.put("text", text);

        HttpEntity<String> request = new HttpEntity<>(reqJsonObject.toString(), headers);
        RestTemplate restTemplate = new RestTemplate();
        String responseJsonStr = restTemplate.postForObject(nerServiceUrl, request, String.class);
        log.debug("Response from ner-proxy-service {}", responseJsonStr);
        return responseJsonStr;
    }

    public String getNERFallback(String text, Throwable t) {
        log.debug("Fallback method called for ner-proxy-service {}", t.getMessage());
        throw new NERServiceNotFoundException("NER Service Not Reachable");
    }
    
}
