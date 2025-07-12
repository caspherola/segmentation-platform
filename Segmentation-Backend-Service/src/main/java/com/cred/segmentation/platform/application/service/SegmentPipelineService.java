package com.cred.segmentation.platform.application.service;

import com.cred.segmentation.platform.application.model.PipelineConfiguration;
import com.cred.segmentation.platform.application.model.RuleDefinitionRequest;
import com.cred.segmentation.platform.application.model.SegmentCreationRequest;
import com.cred.segmentation.platform.application.strategy.DefaultPipelineGenerationStrategy;
import com.cred.segmentation.platform.application.strategy.PipelineGenerationStrategy;
import com.cred.segmentation.platform.application.validator.ExpressionValidator;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SegmentPipelineService {
    private final PipelineGenerationStrategy strategy;
    private final ObjectMapper objectMapper;

    public SegmentPipelineService() {
        this.strategy = new DefaultPipelineGenerationStrategy();
        this.objectMapper = new ObjectMapper();
    }

    public SegmentPipelineService(PipelineGenerationStrategy strategy) {
        this.strategy = strategy;
        this.objectMapper = new ObjectMapper();
    }

    public String generatePipeline(SegmentCreationRequest request) throws Exception {
        // Validate request
        validateRequest(request);

        // Validate expression
        RuleDefinitionRequest ruleRequest = request.getRuleDefinitionRequest();
        ExpressionValidator.ValidationResult validationResult =
                ExpressionValidator.validateExpression(ruleRequest.getExpression(), ruleRequest.getParameters());

        if (!validationResult.isValid()) {
            throw new IllegalArgumentException("Expression validation failed: " + validationResult.getErrorMessage());
        }

        // Generate pipeline
        PipelineConfiguration pipeline = strategy.generatePipeline(request);

        // Convert to JSON
        return objectMapper.writeValueAsString(pipeline);
    }

    private void validateRequest(SegmentCreationRequest request) {
        if (request == null || request.getRuleDefinitionRequest() == null) {
            throw new IllegalArgumentException("Invalid request: SegmentCreationRequest and ruleDefinitionRequest cannot be null");
        }

        RuleDefinitionRequest ruleRequest = request.getRuleDefinitionRequest();

        if (ruleRequest.getName() == null || ruleRequest.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Rule name cannot be null or empty");
        }

        if (ruleRequest.getExpression() == null || ruleRequest.getExpression().trim().isEmpty()) {
            throw new IllegalArgumentException("Expression cannot be null or empty");
        }

        if (ruleRequest.getParameters() == null || ruleRequest.getParameters().isEmpty()) {
            throw new IllegalArgumentException("Parameters cannot be null or empty");
        }
    }
}