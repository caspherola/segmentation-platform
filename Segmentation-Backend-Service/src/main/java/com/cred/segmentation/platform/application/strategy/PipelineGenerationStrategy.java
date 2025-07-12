package com.cred.segmentation.platform.application.strategy;

import com.cred.segmentation.platform.application.model.PipelineConfiguration;
import com.cred.segmentation.platform.application.model.SegmentCreationRequest;

public interface PipelineGenerationStrategy {
    PipelineConfiguration generatePipeline(SegmentCreationRequest request);
}