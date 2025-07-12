package com.cred.segmentation.platform.application.model;

import lombok.Data;

import java.util.List;

@Data
public class RuleDefinitionRequest {
    private String ruleId;
    private String name;
    private String description;
    private String inputEventType;
    private List<Parameter> parameters;
    private String expression;
    private boolean enabled;

    @Data
    public static class Parameter {
        private String name;
        private String type;
        private Object defaultValue;
        private String description;
    }
}
