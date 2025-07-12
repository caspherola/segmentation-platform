package com.cred.segmentation.platform.application.strategy;

import com.cred.segmentation.platform.application.model.*;
import com.cred.segmentation.platform.application.util.ExpressionToSqlConverter;

import java.util.*;

public class PipelineBuilder {
    private PipelineConfiguration pipeline;
    private int stepCounter = 1;

    public PipelineBuilder() {
        this.pipeline = new PipelineConfiguration();
    }

    public PipelineBuilder withRuleSetInfo(String name, String description, String version, String id) {
        pipeline.setRuleSetInfo(new RuleSetInfo(name, description, version, id));
        return this;
    }

    public PipelineBuilder withDataSources(String inputTopic, String outputTopic) {
        List<DataSource> dataSources = new ArrayList<>();

        // Input Kafka source
        Map<String, String> inputParams = new HashMap<>();
        inputParams.put("subscribe", inputTopic);
        inputParams.put("kafka.bootstrap.servers", "localhost:9092");
        dataSources.add(new DataSource("kafka", "kafkaSource", inputParams));

        // Output Kafka source
        Map<String, String> outputParams = new HashMap<>();
        outputParams.put("subscribe", outputTopic);
        outputParams.put("kafka.bootstrap.servers", "localhost:9092");
        dataSources.add(new DataSource("kafka", "kafkaInsightOutput", outputParams));

        if (pipeline.getPipelineRuleDefinition() == null) {
            pipeline.setPipelineRuleDefinition(new PipelineRuleDefinition());
        }
        pipeline.getPipelineRuleDefinition().setDatasource(dataSources);

        return this;
    }

    public PipelineBuilder withPipelineSteps(String expression, List<RuleDefinitionRequest.Parameter> parameters, String segmentId) {
        List<PipelineStep> steps = new ArrayList<>();

        // Step 1: Read from Kafka
        PipelineStep readStep = new PipelineStep("read event stream from kafka", "READ_KAFKA");
        readStep.setOutputStream(Arrays.asList("DATA_READ_KAFKA_STEP" + stepCounter++));
        readStep.getParams().put("sourceName", "kafkaSource");
        readStep.getParams().put("reader.option.startingOffsets", "latest");
        readStep.getParams().put("reader.option.maxOffsetsPerTrigger", "100");
        steps.add(readStep);

        // Step 2: Schema Mapping
        PipelineStep schemaStep = new PipelineStep("apply schema mapping", "SCHEMA_MAPPING");
        schemaStep.setInputStream(Arrays.asList("DATA_READ_KAFKA_STEP1"));
        schemaStep.setOutputStream(Arrays.asList("SCHEMA_APPLIED_STEP" + stepCounter++));
        schemaStep.getParams().put("schema", "kafkaSource");
        steps.add(schemaStep);

        // Step 3: Add Derived Fields
        PipelineStep derivedFieldsStep = new PipelineStep("add derived fields", "ADD_COLUMNS");
        derivedFieldsStep.setInputStream(Arrays.asList("SCHEMA_APPLIED_STEP2"));
        derivedFieldsStep.setOutputStream(Arrays.asList("VARIABLE_PREPARATION_STEP" + stepCounter++));

        // Add column mappings for parameters
        for (RuleDefinitionRequest.Parameter param : parameters) {
            derivedFieldsStep.getParams().put("column." + param.getName(), param.getName());
        }
        steps.add(derivedFieldsStep);

        // Step 4: Evaluate Expression
        PipelineStep expressionStep = new PipelineStep("Evaluate expression", "ADD_COLUMNS");
        expressionStep.setInputStream(Arrays.asList("VARIABLE_PREPARATION_STEP3"));
        expressionStep.setOutputStream(Arrays.asList("SEGMENTATION_EVALUATED_STEP" + stepCounter++));

        String sqlExpression = ExpressionToSqlConverter.convertToSqlCaseStatement(expression, parameters, segmentId);
        expressionStep.getParams().put("column.segmentId", sqlExpression);
        steps.add(expressionStep);

        // Step 5: Filter
        PipelineStep filterStep = new PipelineStep("Filter records that are segmented", "FILTER");
        filterStep.setInputStream(Arrays.asList("SEGMENTATION_EVALUATED_STEP4"));
        filterStep.setOutputStream(Arrays.asList("FILTERED_SEGMENTED_STEP" + stepCounter++));
        filterStep.getParams().put("filterPredicate", "segmentId IS NOT NULL");
        steps.add(filterStep);

        // Step 6: Create Insight Model
        PipelineStep insightStep = new PipelineStep("create uniform insight model", "ADD_COLUMNS");
        insightStep.setInputStream(Arrays.asList("FILTERED_SEGMENTED_STEP5"));
        insightStep.setOutputStream(Arrays.asList("INSIGHT_MODEL_CREATED_STEP" + stepCounter++));
        insightStep.getParams().put("column.value",
                "to_json(struct(uuid() as insightId, segmentId, userId, current_timestamp() as createdAt)) as value");
        insightStep.getParams().put("column.key", "key");
        insightStep.getParams().put("column.topic", "insight-topic");
        steps.add(insightStep);

        // Step 7: Select Columns
        PipelineStep selectStep = new PipelineStep("select columns", "SELECT");
        selectStep.setInputStream(Arrays.asList("INSIGHT_MODEL_CREATED_STEP6"));
        selectStep.setOutputStream(Arrays.asList("INSIGHT_MODEL_CREATED_STEP" + stepCounter++));
        selectStep.getParams().put("columnNames", "key, value, topic");
        steps.add(selectStep);

        // Step 8: Write to Kafka
        PipelineStep writeStep = new PipelineStep("write to kafka", "WRITE_KAFKA");
        writeStep.setInputStream(Arrays.asList("INSIGHT_MODEL_CREATED_STEP7"));
        writeStep.getParams().put("sourceName", "kafkaInsightOutput");
        steps.add(writeStep);

        if (pipeline.getPipelineRuleDefinition() == null) {
            pipeline.setPipelineRuleDefinition(new PipelineRuleDefinition());
        }
        pipeline.getPipelineRuleDefinition().setPipelineFlow(steps);

        return this;
    }

    public PipelineConfiguration build() {
        return pipeline;
    }
}
