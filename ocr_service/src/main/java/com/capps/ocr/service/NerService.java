package com.capps.ocr.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

public interface NerService {
    public JsonNode getNER(String text) throws JsonProcessingException ;
}
