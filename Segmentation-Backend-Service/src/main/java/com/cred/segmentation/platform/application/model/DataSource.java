package com.cred.segmentation.platform.application.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class DataSource {
    private String sourceType;
    private String sourceName;
    private Map<String, String> params;
}