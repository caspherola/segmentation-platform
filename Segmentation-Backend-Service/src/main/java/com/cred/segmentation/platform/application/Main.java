package com.cred.segmentation.platform.application;

import com.cred.segmentation.platform.application.model.RuleDefinitionRequest;
import com.cred.segmentation.platform.application.model.SegmentCreationRequest;
import com.cred.segmentation.platform.application.service.SegmentPipelineService;

import java.util.ArrayList;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        try {
            // Create sample request
            SegmentCreationRequest request = createSampleRequest();

            // Create service
            SegmentPipelineService service = new SegmentPipelineService();

            // Generate pipeline
            String pipelineJson = service.generatePipeline(request);

            System.out.println("Generated Pipeline JSON:");
            System.out.println(pipelineJson);

        } catch (Exception e) {
            System.err.println("Error generating pipeline: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static SegmentCreationRequest createSampleRequest() {
        // Create parameters
        List<RuleDefinitionRequest.Parameter> parameters = new ArrayList<>();

        RuleDefinitionRequest.Parameter amountParam = new RuleDefinitionRequest.Parameter();
        amountParam.setName("amount");
        amountParam.setType("number");
        amountParam.setDescription("Transaction amount");
        parameters.add(amountParam);

        RuleDefinitionRequest.Parameter reading = new RuleDefinitionRequest.Parameter();
        reading.setName("reading");
        reading.setType("number");
        reading.setDescription("Transaction reading");
        parameters.add(reading);

        RuleDefinitionRequest.Parameter typeParam = new RuleDefinitionRequest.Parameter();
        typeParam.setName("transactionType");
        typeParam.setType("string");
        typeParam.setDescription("Transaction type");
        parameters.add(typeParam);

        // Create rule definition
        RuleDefinitionRequest ruleRequest = new RuleDefinitionRequest();
        ruleRequest.setRuleId("RULE_001");
        ruleRequest.setName("Transaction Segmentation Rule");
        ruleRequest.setDescription("Segments transactions based on amount and type");
        ruleRequest.setInputEventType("transaction");
        ruleRequest.setParameters(parameters);
        ruleRequest.setExpression("amount > 100 and transactionType == 'credit' or amount < 50 and reading < 10");
        ruleRequest.setEnabled(true);

        // Create main request
        SegmentCreationRequest request = new SegmentCreationRequest();
        request.setRuleDefinitionRequest(ruleRequest);

        return request;
    }
}