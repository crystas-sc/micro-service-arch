package com.capps.ocr.service;

import java.io.File;

import com.lowagie.text.pdf.OcspClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.util.LoadLibs;

import org.springframework.core.io.Resource;

@Slf4j
@Service
public class OCRServiceImpl implements OCRService {
    @Autowired
    FilesStorageService storageService;
    Tesseract tesseract;

    public OCRServiceImpl() {
        tesseract = new Tesseract();
        File tessDataFolder = LoadLibs.extractTessResources("tessdata");
        tesseract.setDatapath(tessDataFolder.getPath());
        tesseract.setPageSegMode(1);
        tesseract.setTessVariable("textord_min_linesize", "2.5");
    }

    @Override
    public String getOCR(String fileName) throws Exception {
        log.info("fileName: {}", fileName);
        Resource loadedFile = storageService.load(fileName);
        // File image = new File(fileName);
        File image = loadedFile.getFile();
        // Tesseract tesseract = new Tesseract();
        // // tesseract.setDatapath("src/main/resources/tessdata");
        // tesseract.setLanguage("eng");
        // tesseract.setPageSegMode(1);
        // tesseract.setOcrEngineMode(1);
        String result = tesseract.doOCR(image);
        log.info("result {}", result);
        return result;
    }

}
