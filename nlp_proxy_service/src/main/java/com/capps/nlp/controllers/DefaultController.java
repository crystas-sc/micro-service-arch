package com.capps.nlp.controllers;

import java.util.List;
import java.util.Map;

import com.capps.nlp.models.NERRequest;
import com.capps.nlp.service.NERService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@ResponseBody
@Slf4j
public class DefaultController {
    @Autowired
    NERService nerService;

    @PostMapping("/get-ner")
    List<Map<String, String>> getNer(@RequestBody NERRequest nerRequest) throws Exception {

        log.debug("Request  string:  {} ", nerRequest.getText());

        return nerService.getNer(nerRequest.getText());

    }

}
