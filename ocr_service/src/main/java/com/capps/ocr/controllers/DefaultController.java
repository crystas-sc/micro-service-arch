package com.capps.ocr.controllers;

import com.capps.ocr.service.FilesStorageService;
import com.capps.ocr.service.NerService;
import com.capps.ocr.service.OCRService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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
    public String hello(){
        return "Hello World!";
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        String message = "";
        try {
            String storedFileName = storageService.save(file);

            message = "Uploaded the file successfully: " + file.getOriginalFilename();
            log.info("Sending request to OCR processing");
            message =  ocrService.getOCR(storedFileName);
            log.info("ocr successfull, requesting ner-proxy-service");
            message = nerService.getNER(message);
            return ResponseEntity.status(HttpStatus.OK).body(message);
        } catch (Exception e) {
            log.error("exception {}",e);
            // message = "Error in processing uploaded file: " + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
