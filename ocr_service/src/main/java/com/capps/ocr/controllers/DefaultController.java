package com.capps.ocr.controllers;

import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.Optional;

import com.capps.ocr.service.FilesStorageService;
import com.capps.ocr.service.NerService;
import com.capps.ocr.service.OCRService;
import com.capps.ocr.service.RedisService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Controller
@ResponseBody
@Slf4j
public class DefaultController {
    @Autowired
    FilesStorageService storageService;

    @Autowired
    OCRService ocrService;

    @Autowired
    NerService nerService;

    @Autowired
    RedisService redisService;

    @Autowired
    ObjectMapper objectMapper;

    @GetMapping("/")
    public String hello() {
        return "Hello World!";
    }

    @PostMapping("/upload")
    @CrossOrigin
    public JsonNode uploadFile(@RequestParam("file") MultipartFile file) throws JsonProcessingException, Exception {
        String storedFileName = storageService.save(file);
        byte[] b = Files.readAllBytes(storageService.load(storedFileName).getFile().toPath());
        byte[] hash = MessageDigest.getInstance("MD5").digest(b);
        String strHash = new String(Base64.encodeBase64(hash));
        Optional<JsonNode> cached = redisService.getStoredJson(strHash);
        if (cached.isPresent()) {
            return cached.get();
        }

        log.debug("base64 hash text {}", strHash);
        log.debug("Sending request to OCR processing");
        String message = ocrService.getOCR(storedFileName);
        log.info("ocr successfull, requesting ner-proxy-service");
        JsonNode resp = nerService.getNER(message);
        log.info("ner-proxy-service successfull, storing response in redis");
        redisService.set(strHash, resp.toString(), 60 * 60 * 24);
        return resp;

    }
}
