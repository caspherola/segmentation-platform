package com.cred.segmentation.platform.application.model;

import lombok.Data;

import java.util.Map;

@Data
public class PipelineConfiguration {
    private RuleSetInfo ruleSetInfo;
    private PipelineRuleDefinition pipelineRuleDefinition;
    private Map<String, String> sparkContextConfig;

}
