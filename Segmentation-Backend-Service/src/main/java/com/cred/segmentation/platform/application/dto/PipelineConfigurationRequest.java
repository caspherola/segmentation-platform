package com.cred.segmentation.platform.application.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class PipelineConfigurationRequest {
    @NotBlank
    private String partitionKey;

    @NotNull
    private Object configData;

    // Constructors
    public PipelineConfigurationRequest() {}

    public PipelineConfigurationRequest(String partitionKey, Object configData) {
        this.partitionKey = partitionKey;
        this.configData = configData;
    }
}
