package com.cred.segmentation.platform.application.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RuleDefinitionRequest {
    @JsonProperty("ruleId")
    private String ruleId;
    @NotBlank(message = "Name cannot be blank")
    private String name;
    private String description;
    @NotBlank(message = "Input event type cannot be blank")
    @JsonProperty("inputEventType")
    private String inputEventType;
    private List<Parameter> parameters;
    @NotBlank(message = "Expression cannot be blank")
    @JsonProperty("expression")
    private String expression;
    private boolean enabled;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Parameter {
        @NotBlank(message = "Parameter name cannot be blank")
        private String name;
        @NotBlank(message = "Parameter type cannot be blank")
        private String type;
        private String defaultValue;
        private String description;
    }
}
