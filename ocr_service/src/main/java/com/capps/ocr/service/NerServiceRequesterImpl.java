package com.capps.ocr.service;

import com.capps.ocr.exceptions.NERServiceNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @Override
    @Retry(name = "nerService", fallbackMethod = "getNERFallback")
    @CircuitBreaker(name = "nerServiceCircuitBreaker" , fallbackMethod = "getNERFallback")
    public JsonNode getNER(String text) throws JsonProcessingException {
        log.debug("Sending request to NER proxy service");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject reqJsonObject = new JSONObject();
        reqJsonObject.put("text", text);

        HttpEntity<String> request = new HttpEntity<>(reqJsonObject.toString(), headers);
        String responseJsonStr = restTemplate.postForObject(nerServiceUrl, request, String.class);
        log.debug("Response from ner-proxy-service {}", responseJsonStr);
        JsonNode root = objectMapper.readTree(responseJsonStr);
        return root;
    }

    public JsonNode getNERFallback(String text, Throwable t) {
        log.debug("Fallback method called for ner-proxy-service {}", t.getMessage());
        throw new NERServiceNotFoundException("NER Service Not Reachable");
    }
    
}
