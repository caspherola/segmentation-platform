package com.cred.segmentation.platform.application.model;

import lombok.Data;

import java.util.List;

@Data
public class PipelineRuleDefinition {
    private List<DataSource> datasource;
    private List<PipelineStep> pipelineFlow;
}
