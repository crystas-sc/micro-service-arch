package com.capps.ocr.service;

import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import brave.Span;
import brave.Tracer;
import brave.Tracer.SpanInScope;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
@Slf4j
public class RedisService {
    @Autowired
    JedisPool jedisPool;

    @Autowired
    FilesStorageService storageService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private Tracer tracer;

    public Optional<String> get(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return Optional.ofNullable(jedis.get(key));
        }
    }

    public void set(String key, String value, int ttl) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.setex(key, ttl, value);
        }
    }

    public Optional<JsonNode> getStoredJson(String strHash) throws Exception {
        Span newSpan = tracer.nextSpan().name("redis-cache-get");
        try (SpanInScope ws = tracer.withSpanInScope(newSpan.start())) {
            Optional<String> cached = this.get(strHash);
            if (cached.isPresent()) {
                log.info("Found cached result for {} : {}", strHash, cached.get());
                return Optional.of(objectMapper.readTree(cached.get()));
            }
            return Optional.empty();
        } finally {
            newSpan.finish();
        }

    }
}
