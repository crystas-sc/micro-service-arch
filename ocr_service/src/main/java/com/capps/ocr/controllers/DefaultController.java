package com.capps.ocr.controllers;

import java.util.Map;

import com.capps.ocr.service.FilesStorageService;
import com.capps.ocr.service.NerService;
import com.capps.ocr.service.OCRService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/")
    public String hello() {
        return "Hello World!";
    }

    @PostMapping("/upload")
    @CrossOrigin
    public JsonNode uploadFile(@RequestParam("file") MultipartFile file) throws JsonProcessingException, Exception {
        String storedFileName = storageService.save(file);
        log.info("Sending request to OCR processing");
        String message = ocrService.getOCR(storedFileName);
        log.info("ocr successfull, requesting ner-proxy-service");
        return nerService.getNER(message);

    }
}
