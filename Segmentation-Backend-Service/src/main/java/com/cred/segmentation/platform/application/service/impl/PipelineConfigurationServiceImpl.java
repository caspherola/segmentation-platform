package com.cred.segmentation.platform.application.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.cred.segmentation.platform.application.dto.PipelineConfigurationRequest;
import com.cred.segmentation.platform.application.dto.PipelineConfigurationResponse;
import com.cred.segmentation.platform.application.entity.PipelineConfigurationEntity;
import com.cred.segmentation.platform.application.exception.PipelineConfigurationException;
import com.cred.segmentation.platform.application.repository.PipelineConfigurationRepository;
import com.cred.segmentation.platform.application.service.PipelineConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class PipelineConfigurationServiceImpl implements PipelineConfigurationService {

    private static final Logger logger = LoggerFactory.getLogger(PipelineConfigurationServiceImpl.class);

    private final PipelineConfigurationRepository repository;
    private final ObjectMapper objectMapper;

    @Autowired
    public PipelineConfigurationServiceImpl(PipelineConfigurationRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Override
    public PipelineConfigurationResponse createConfiguration(PipelineConfigurationRequest request) {
        logger.info("Creating pipeline configuration for partition key: {}", request.getPartitionKey());

        if (repository.existsByPartitionKey(request.getPartitionKey())) {
            throw new PipelineConfigurationException("Configuration already exists for partition key: " + request.getPartitionKey());
        }

        try {
            String configDataJson = objectMapper.writeValueAsString(request.getConfigData());
            PipelineConfigurationEntity entity = new PipelineConfigurationEntity(request.getPartitionKey(), configDataJson);

            PipelineConfigurationEntity savedEntity = repository.save(entity);
            logger.info("Successfully created pipeline configuration for partition key: {}", request.getPartitionKey());

            return mapToResponse(savedEntity);
        } catch (JsonProcessingException e) {
            logger.error("Error serializing configuration data for partition key: {}", request.getPartitionKey(), e);
            throw new PipelineConfigurationException("Error processing configuration data", e);
        }
    }

    @Override
    public PipelineConfigurationResponse updateConfiguration(String partitionKey, PipelineConfigurationRequest request) {
        logger.info("Updating pipeline configuration for partition key: {}", partitionKey);

        Optional<PipelineConfigurationEntity> existingEntity = repository.findByPartitionKey(partitionKey);
        if (existingEntity.isEmpty()) {
            throw new PipelineConfigurationException("Configuration not found for partition key: " + partitionKey);
        }

        try {
            String configDataJson = objectMapper.writeValueAsString(request.getConfigData());
            PipelineConfigurationEntity entity = existingEntity.get();
            entity.setConfigData(configDataJson);

            PipelineConfigurationEntity savedEntity = repository.save(entity);
            logger.info("Successfully updated pipeline configuration for partition key: {}", partitionKey);

            return mapToResponse(savedEntity);
        } catch (JsonProcessingException e) {
            logger.error("Error serializing configuration data for partition key: {}", partitionKey, e);
            throw new PipelineConfigurationException("Error processing configuration data", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PipelineConfigurationResponse> getConfigurationByPartitionKey(String partitionKey) {
        logger.debug("Fetching pipeline configuration for partition key: {}", partitionKey);

        return repository.findByPartitionKey(partitionKey)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PipelineConfigurationResponse> getAllConfigurations() {
        logger.debug("Fetching all pipeline configurations");

        return repository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PipelineConfigurationResponse> getConfigurationsByKeyPattern(String keyPattern) {
        logger.debug("Fetching pipeline configurations by key pattern: {}", keyPattern);

        return repository.findByPartitionKeyContaining(keyPattern)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PipelineConfigurationResponse> getConfigurationsByRuleId(String ruleId) {
        logger.debug("Fetching pipeline configurations by rule ID: {}", ruleId);

        return repository.findByRuleId(ruleId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PipelineConfigurationResponse> getConfigurationsByRuleName(String ruleName) {
        logger.debug("Fetching pipeline configurations by rule name: {}", ruleName);

        return repository.findByRuleName(ruleName)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteConfiguration(String partitionKey) {
        logger.info("Deleting pipeline configuration for partition key: {}", partitionKey);

        if (!repository.existsByPartitionKey(partitionKey)) {
            logger.warn("Configuration not found for partition key: {}", partitionKey);
            return false;
        }

        repository.deleteById(partitionKey);
        logger.info("Successfully deleted pipeline configuration for partition key: {}", partitionKey);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean configurationExists(String partitionKey) {
        return repository.existsByPartitionKey(partitionKey);
    }

    private PipelineConfigurationResponse mapToResponse(PipelineConfigurationEntity entity) {
        try {
            Object configData = objectMapper.readValue(entity.getConfigData(), Object.class);
            PipelineConfigurationResponse response = new PipelineConfigurationResponse(entity.getPartitionKey(), configData);
            response.setCreatedAt(entity.getCreatedAt());
            response.setUpdatedAt(entity.getUpdatedAt());
            response.setVersion(entity.getVersion());
            return response;
        } catch (JsonProcessingException e) {
            logger.error("Error deserializing configuration data for partition key: {}", entity.getPartitionKey(), e);
            throw new PipelineConfigurationException("Error processing configuration data", e);
        }
    }
}

