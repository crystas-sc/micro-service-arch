package com.capps.nlp.service;

import java.util.List;
import java.util.Map;

public interface NERService {

    public List<Map<String, String>> getNer(String text) throws Exception;

}
