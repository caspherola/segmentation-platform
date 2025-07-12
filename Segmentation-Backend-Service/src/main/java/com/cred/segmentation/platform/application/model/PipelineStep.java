package com.cred.segmentation.platform.application.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class PipelineStep {
    private String stepName;
    private String stepType;
    private List<String> inputStream;
    private List<String> outputStream;
    private Map<String, String> params;

    public PipelineStep(String stepName, String stepType) {
        this.stepName = stepName;
        this.stepType = stepType;
        this.inputStream = new ArrayList<>();
        this.outputStream = new ArrayList<>();
        this.params = new HashMap<>();
    }
}