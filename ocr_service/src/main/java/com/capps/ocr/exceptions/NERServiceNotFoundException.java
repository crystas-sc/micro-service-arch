package com.capps.ocr.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="NER Service Not Reachable")  // 509
public class NERServiceNotFoundException  extends RuntimeException {
    public NERServiceNotFoundException(String msg){
        super(msg);
    }
}
