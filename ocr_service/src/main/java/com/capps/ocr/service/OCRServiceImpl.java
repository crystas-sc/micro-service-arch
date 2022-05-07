package com.capps.ocr.service;

import java.io.File;

import com.lowagie.text.pdf.OcspClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import brave.Span;
import brave.Tracer;
import brave.Tracer.SpanInScope;
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
    @Autowired
    private Tracer tracer;

    public OCRServiceImpl() {
        tesseract = new Tesseract();
        File tessDataFolder = LoadLibs.extractTessResources("tessdata");
        tesseract.setDatapath(tessDataFolder.getPath());
        tesseract.setPageSegMode(1);
        tesseract.setTessVariable("textord_min_linesize", "2.5");
    }

    @Override
    public String getOCR(String fileName) throws Exception {
        log.debug("ocr for fileName: {}", fileName);
        Span newSpan = tracer.nextSpan().name("ocr-processing");

        try (SpanInScope ws = tracer.withSpanInScope(newSpan.start())) {
            Resource loadedFile = storageService.load(fileName);
            File image = loadedFile.getFile();
            String result = tesseract.doOCR(image);
            log.info("result {}", result);
            return result;
        } finally {
            newSpan.finish();
        }
    }

}
