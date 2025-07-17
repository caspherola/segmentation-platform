package com.cred.segmentation.platform.application.strategy;

import com.cred.segmentation.platform.application.dto.EventsStreamOutputDto;
import com.cred.segmentation.platform.application.dto.PipelineConfigurationResponse;
import com.cred.segmentation.platform.application.model.PipelineConfiguration;
import com.cred.segmentation.platform.application.model.RuleDefinitionRequest;
import com.cred.segmentation.platform.application.model.SegmentCreationRequest;
import com.cred.segmentation.platform.application.service.impl.DataIngestionOnboardingServiceImpl;
import com.cred.segmentation.platform.application.service.impl.PipelineConfigurationServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DefaultPipelineGenerationStrategy implements PipelineGenerationStrategy {
    private final DataIngestionOnboardingServiceImpl eventService;

    public DefaultPipelineGenerationStrategy(DataIngestionOnboardingServiceImpl eventService) {
        this.eventService = eventService;
    }

    @Override
    public PipelineConfiguration generatePipeline(SegmentCreationRequest request) {
        RuleDefinitionRequest ruleRequest = request.getRuleDefinitionRequest();
        EventsStreamOutputDto outputDto= eventService.getEventById(ruleRequest.getInputEventId());

        if(request.getRuleDefinitionRequest().getWindowAggregation()!=null && request.getRuleDefinitionRequest().getWindowAggregation().getPrimaryKey()!=null){
            return createAggregationFlow(ruleRequest, outputDto, request.getRuleDefinitionRequest().getWindowAggregation());
        }

        return createExpressionFlow(ruleRequest, outputDto);
    }

   private  PipelineConfiguration createAggregationFlow(RuleDefinitionRequest ruleRequest, EventsStreamOutputDto outputDto, RuleDefinitionRequest.WindowAggregation aggregation){
        return new PipelineBuilder()
                .withRuleSetInfo(
                        ruleRequest.getName() + " Pipeline",
                        ruleRequest.getDescription(),
                        "1.0.0",
                        ruleRequest.getRuleId()
                )
                .withDataSources(
                        outputDto,
                        "segmentation-output-topic"
                )
                .withPipelineStepsAndLastAggregation(
                        ruleRequest.getExpression(),
                        ruleRequest.getParameters(),
                        ruleRequest.getRuleId(),
                        outputDto.getId(),
                        aggregation
                )
                .build();
   }

    private  PipelineConfiguration createExpressionFlow(RuleDefinitionRequest ruleRequest, EventsStreamOutputDto outputDto){
        return new PipelineBuilder()
                .withRuleSetInfo(
                        ruleRequest.getName() + " Pipeline",
                        ruleRequest.getDescription(),
                        "1.0.0",
                        ruleRequest.getRuleId()
                )
                .withDataSources(
                        outputDto,
                        "segmentation-output-topic"
                )
                .withPipelineSteps(
                        ruleRequest.getExpression(),
                        ruleRequest.getParameters(),
                        ruleRequest.getRuleId(),
                        outputDto.getId()
                )
                .build();
    }
}
