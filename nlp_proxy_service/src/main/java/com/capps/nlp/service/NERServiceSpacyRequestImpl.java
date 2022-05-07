package com.capps.nlp.service;

import java.util.List;
import java.util.Map;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.capps.nlp.exceptions.RateLimitException;
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

import brave.Span;
import brave.Tracer;
import brave.Tracer.SpanInScope;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class NERServiceSpacyRequestImpl implements NERService {
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    ObjectMapper objectMapper;
    @Value("${spring.application.spacyApiUrl}")
    String spacyApiUrl;

    @Autowired
    private Tracer tracer;

    @Override
    @Bulkhead(name = "nerRateLimiter", fallbackMethod = "getDefaultNERResponse")
    public List<Map<String, String>> getNer(String text) throws JsonProcessingException {
        JsonNode root = objectMapper.readTree(sendReqForNERProcessing(text));
        Span newSpan = tracer.nextSpan().name("ner-data-processing");
        try (SpanInScope ws = tracer.withSpanInScope(newSpan.start())) {
            List<Map<String, String>> result = StreamSupport
                    .stream(Spliterators.spliteratorUnknownSize(root.elements(), 0), false)
                    .map(x -> Map.of("text", text.substring(x.get("start").asInt(), x.get("end").asInt()),
                            "label", x.get("label").asText()))
                    .collect(Collectors.toList());
            log.debug("NER Response output: {} ", result);
            return result;
        } finally {
            newSpan.finish();
        }

    }

    private String sendReqForNERProcessing(String text) {
        Span newSpan = tracer.nextSpan().name("spacyOnlineRequest");
        String responseJsonStr = null;
        try (SpanInScope ws = tracer.withSpanInScope(newSpan.start())) {

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setContentType(MediaType.APPLICATION_JSON);
            JSONObject reqJsonObject = new JSONObject();
            reqJsonObject.put("model", "en_core_web_sm");
            reqJsonObject.put("text", text);

            HttpEntity<String> request = new HttpEntity<>(reqJsonObject.toString(), headers);

            responseJsonStr = restTemplate.postForObject(spacyApiUrl, request, String.class);
            log.info("Response from spacy {}", responseJsonStr);
        } finally {
            newSpan.finish();

        }
        return responseJsonStr;
    }

    public List<Map<String, String>> getDefaultNERResponse(String s, Throwable throwable) {
        log.info("Request Rate limit for NER reached; default ner response called");

        throw new RateLimitException("Rate limit exceeded");
        // return new ArrayList<>();
    }

}
