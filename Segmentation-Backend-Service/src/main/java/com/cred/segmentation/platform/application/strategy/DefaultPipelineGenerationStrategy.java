package com.cred.segmentation.platform.application.strategy;

import com.cred.segmentation.platform.application.model.PipelineConfiguration;
import com.cred.segmentation.platform.application.model.RuleDefinitionRequest;
import com.cred.segmentation.platform.application.model.SegmentCreationRequest;

public class DefaultPipelineGenerationStrategy implements PipelineGenerationStrategy {
    @Override
    public PipelineConfiguration generatePipeline(SegmentCreationRequest request) {
        RuleDefinitionRequest ruleRequest = request.getRuleDefinitionRequest();

        return new PipelineBuilder()
                .withRuleSetInfo(
                        ruleRequest.getName() + " Pipeline",
                        ruleRequest.getDescription(),
                        "1.0.0",
                        ruleRequest.getRuleId()
                )
                .withDataSources(
                        ruleRequest.getInputEventType() + "-topic",
                        "segmentation-output-topic"
                )
                .withPipelineSteps(
                        ruleRequest.getExpression(),
                        ruleRequest.getParameters(),
                        ruleRequest.getRuleId()
                )
                .build();
    }
}
