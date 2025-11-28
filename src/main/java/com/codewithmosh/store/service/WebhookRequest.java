package com.codewithmosh.store.service;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WebhookRequest {
    private Map<String,String> header;
    private String payload;
     
}
