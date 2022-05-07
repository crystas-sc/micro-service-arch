package com.capps.nlp.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.BANDWIDTH_LIMIT_EXCEEDED, reason="Too many requests")  // 509
public class RateLimitException  extends RuntimeException {
    public RateLimitException(String msg){
        super(msg);
    }
}
