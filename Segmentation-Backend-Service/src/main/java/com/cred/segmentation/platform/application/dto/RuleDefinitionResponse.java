package com.cred.segmentation.platform.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RuleDefinitionResponse {

    private UUID id;
    private String ruleId;
    private String name;
    private String description;
    private String inputEventType;
    private List<ParameterResponse> parameters;
    private String expression;
    private Boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ParameterResponse {
        private UUID id;
        private String name;
        private String type;
        private String defaultValue;
        private String description;
    }
}