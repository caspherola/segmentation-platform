package com.cred.segmentation.platform.application.model;

import lombok.Data;

@Data
public class PipelineConfiguration {
    private RuleSetInfo ruleSetInfo;
    private PipelineRuleDefinition pipelineRuleDefinition;
}
