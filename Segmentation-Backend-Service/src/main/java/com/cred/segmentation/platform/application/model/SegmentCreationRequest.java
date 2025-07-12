package com.cred.segmentation.platform.application.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Builder;


@Data
public class SegmentCreationRequest {
    @JsonProperty("ruleDefinitionRequest")
    private RuleDefinitionRequest ruleDefinitionRequest;
}