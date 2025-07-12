package com.cred.segmentation.platform.application.controller;

import com.cred.segmentation.platform.application.dto.PipelineConfigurationRequest;
import com.cred.segmentation.platform.application.dto.PipelineConfigurationResponse;
import com.cred.segmentation.platform.application.service.PipelineConfigurationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/pipeline-configurations")
@CrossOrigin(origins = "*")
public class PipelineConfigurationController {

    private static final Logger logger = LoggerFactory.getLogger(PipelineConfigurationController.class);

    private final PipelineConfigurationService service;

    @Autowired
    public PipelineConfigurationController(PipelineConfigurationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<PipelineConfigurationResponse> createConfiguration(
            @Valid @RequestBody PipelineConfigurationRequest request) {
        logger.info("REST request to create pipeline configuration for partition key: {}", request.getPartitionKey());

        PipelineConfigurationResponse response = service.createConfiguration(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{partitionKey}")
    public ResponseEntity<PipelineConfigurationResponse> updateConfiguration(
            @PathVariable String partitionKey,
            @Valid @RequestBody PipelineConfigurationRequest request) {
        logger.info("REST request to update pipeline configuration for partition key: {}", partitionKey);

        PipelineConfigurationResponse response = service.updateConfiguration(partitionKey, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{partitionKey}")
    public ResponseEntity<PipelineConfigurationResponse> getConfiguration(
            @PathVariable String partitionKey) {
        logger.info("REST request to get pipeline configuration for partition key: {}", partitionKey);

        Optional<PipelineConfigurationResponse> response = service.getConfigurationByPartitionKey(partitionKey);
        return response.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<PipelineConfigurationResponse>> getAllConfigurations() {
        logger.info("REST request to get all pipeline configurations");

        List<PipelineConfigurationResponse> responses = service.getAllConfigurations();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/search")
    public ResponseEntity<List<PipelineConfigurationResponse>> searchConfigurations(
            @RequestParam(required = false) String keyPattern,
            @RequestParam(required = false) String ruleId,
            @RequestParam(required = false) String ruleName) {
        logger.info("REST request to search pipeline configurations with keyPattern: {}, ruleId: {}, ruleName: {}",
                keyPattern, ruleId, ruleName);

        List<PipelineConfigurationResponse> responses;

        if (keyPattern != null) {
            responses = service.getConfigurationsByKeyPattern(keyPattern);
        } else if (ruleId != null) {
            responses = service.getConfigurationsByRuleId(ruleId);
        } else if (ruleName != null) {
            responses = service.getConfigurationsByRuleName(ruleName);
        } else {
            responses = service.getAllConfigurations();
        }

        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{partitionKey}")
    public ResponseEntity<Void> deleteConfiguration(@PathVariable String partitionKey) {
        logger.info("REST request to delete pipeline configuration for partition key: {}", partitionKey);

        boolean deleted = service.deleteConfiguration(partitionKey);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/{partitionKey}/exists")
    public ResponseEntity<Boolean> configurationExists(@PathVariable String partitionKey) {
        logger.info("REST request to check if pipeline configuration exists for partition key: {}", partitionKey);

        boolean exists = service.configurationExists(partitionKey);
        return ResponseEntity.ok(exists);
    }
}

