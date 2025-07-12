package com.cred.segmentation.platform.application.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PipelineConfigurationResponse {
    @NotBlank
    private String partitionKey;

    @NotNull
    private Object configData;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long version;

    // Constructors
    public PipelineConfigurationResponse() {}

    public PipelineConfigurationResponse(String partitionKey, Object configData) {
        this.partitionKey = partitionKey;
        this.configData = configData;
    }
}
