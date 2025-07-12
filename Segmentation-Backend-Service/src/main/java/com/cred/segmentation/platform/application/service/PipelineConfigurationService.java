package com.cred.segmentation.platform.application.service;
import com.cred.segmentation.platform.application.model.PipelineConfiguration;
import com.cred.segmentation.platform.application.dto.PipelineConfigurationRequest;
import com.cred.segmentation.platform.application.dto.PipelineConfigurationResponse;

import java.util.List;
import java.util.Optional;
public interface PipelineConfigurationService {

    PipelineConfigurationResponse createConfiguration(PipelineConfigurationRequest request);

    PipelineConfigurationResponse updateConfiguration(String partitionKey, PipelineConfigurationRequest request);

    Optional<PipelineConfigurationResponse> getConfigurationByPartitionKey(String partitionKey);

    List<PipelineConfigurationResponse> getAllConfigurations();

    List<PipelineConfigurationResponse> getConfigurationsByKeyPattern(String keyPattern);

    List<PipelineConfigurationResponse> getConfigurationsByRuleId(String ruleId);

    List<PipelineConfigurationResponse> getConfigurationsByRuleName(String ruleName);

    boolean deleteConfiguration(String partitionKey);

    boolean configurationExists(String partitionKey);
}
