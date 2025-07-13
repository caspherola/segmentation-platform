package com.cred.segmentation.platform.application.service;

import com.cred.segmentation.platform.application.dto.PipelineConfigurationRequest;
import com.cred.segmentation.platform.application.dto.PipelineConfigurationResponse;
import com.cred.segmentation.platform.application.dto.RuleDefinitionResponse;
import com.cred.segmentation.platform.application.entity.RuleDefinition;
import com.cred.segmentation.platform.application.entity.RuleParameter;
import com.cred.segmentation.platform.application.exception.RuleDefinitionNotFoundException;
import com.cred.segmentation.platform.application.exception.DuplicateRuleIdException;
import com.cred.segmentation.platform.application.mapper.RuleDefinitionMapper;
import com.cred.segmentation.platform.application.model.RuleDefinitionRequest;
import com.cred.segmentation.platform.application.model.SegmentCreationRequest;
import com.cred.segmentation.platform.application.repository.RuleDefinitionRepository;
import com.cred.segmentation.platform.application.repository.RuleParameterRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class RuleDefinitionService {

    private final RuleDefinitionRepository repository;
    private final RuleParameterRespository  ruleParameterRespository;
    private final RuleDefinitionMapper mapper;
    private final SegmentPipelineService pipelineService;
    private final PipelineConfigurationService pipelineConfigurationService;


    @Autowired
    public RuleDefinitionService(RuleDefinitionRepository repository, RuleParameterRespository ruleParameterRespository, RuleDefinitionMapper mapper, SegmentPipelineService pipelineService, PipelineConfigurationService pipelineConfigurationService) {
        this.repository = repository;
        this.ruleParameterRespository = ruleParameterRespository;
        this.mapper = mapper;
        this.pipelineService = pipelineService;
        this.pipelineConfigurationService = pipelineConfigurationService;
    }

    public RuleDefinitionResponse createRule(SegmentCreationRequest request) {
        validateRuleRequest(request);


        String ruleId = request.getRuleDefinitionRequest().getRuleId();
        if (ruleId != null && repository.existsByRuleId(ruleId)) {
            throw new DuplicateRuleIdException("Rule with ID '" + ruleId + "' already exists");
        }

        RuleDefinition entity = mapper.toEntity(request);
        if (entity.getRuleId() == null) {
            entity.setRuleId(UUID.randomUUID().toString());
        }
        RuleDefinition savedEntity = repository.save(entity);
        // Add parameters using the convenience method
        for (RuleDefinitionRequest.Parameter dto : request.getRuleDefinitionRequest().getParameters()) {
            RuleParameter parameter = new RuleParameter(
                    dto.getName(),
                    dto.getType(),
                    dto.getDescription(),
                    savedEntity.getId()
            );
            ruleParameterRespository.save(parameter);
        }
        try {
            String partitionKey=request.getRuleDefinitionRequest().getInputEventId()+ "-" + request.getRuleDefinitionRequest().getInputEventType();
            String pipeline=pipelineService.generatePipeline(request);
            PipelineConfigurationResponse pipelineConfigurationResponse=pipelineConfigurationService.createConfiguration
                    (new PipelineConfigurationRequest(savedEntity.getRuleId(), pipeline));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return mapper.toResponse(savedEntity);
    }

//    @Transactional(readOnly = true)
//    public RuleDefinitionResponse getRuleById(UUID id) {
//        RuleDefinition entity = repository.findByIdWithParameters(id)
//                .orElseThrow(() -> new RuleDefinitionNotFoundException("Rule not found with ID: " + id));
//        return mapper.toResponse(entity);
//    }
//
//    @Transactional(readOnly = true)
//    public RuleDefinitionResponse getRuleByRuleId(String ruleId) {
//        RuleDefinition entity = repository.findByRuleIdWithParameters(ruleId)
//                .orElseThrow(() -> new RuleDefinitionNotFoundException("Rule not found with rule ID: " + ruleId));
//        return mapper.toResponse(entity);
//    }

    @Transactional(readOnly = true)
    public List<RuleDefinitionResponse> getAllRules() {
        List<RuleDefinition> entities = repository.findAll();
        return mapper.toResponseList(entities);
    }

    @Transactional(readOnly = true)
    public List<RuleDefinitionResponse> getRulesByEnabled(Boolean enabled) {
        List<RuleDefinition> entities = repository.findByEnabled(enabled);
        return mapper.toResponseList(entities);
    }

    @Transactional(readOnly = true)
    public List<RuleDefinitionResponse> getRulesByInputEventType(String inputEventType) {
        List<RuleDefinition> entities = repository.findByInputEventType(inputEventType);
        return mapper.toResponseList(entities);
    }

//    public RuleDefinitionResponse updateRule(UUID id, SegmentCreationRequest request) {
//        validateRuleRequest(request);
//
//        RuleDefinition existingEntity = repository.findByIdWithParameters(id)
//                .orElseThrow(() -> new RuleDefinitionNotFoundException("Rule not found with ID: " + id));
//
//        String newRuleId = request.getRuleDefinitionRequest().getRuleId();
//        if (newRuleId != null && !newRuleId.equals(existingEntity.getRuleId()) && repository.existsByRuleId(newRuleId)) {
//            throw new DuplicateRuleIdException("Rule with ID '" + newRuleId + "' already exists");
//        }
//
//
//
//        // Update entity from request
//        mapper.updateEntityFromRequest(request, existingEntity);
//
//        RuleDefinition updatedEntity = repository.save(existingEntity);
//        return mapper.toResponse(updatedEntity);
//    }

    public void deleteRule(UUID id) {
        if (!repository.existsById(id)) {
            throw new RuleDefinitionNotFoundException("Rule not found with ID: " + id);
        }
        repository.deleteById(id);
    }

    public void deleteRuleByRuleId(String ruleId) {
        RuleDefinition entity = repository.findByRuleId(ruleId)
                .orElseThrow(() -> new RuleDefinitionNotFoundException("Rule not found with rule ID: " + ruleId));
        repository.delete(entity);
    }

    private void validateRuleRequest(SegmentCreationRequest request) {
        if (request.getRuleDefinitionRequest() == null) {
            throw new IllegalArgumentException("Rule definition request cannot be null");
        }

        var ruleData = request.getRuleDefinitionRequest();
        if (ruleData.getName() == null || ruleData.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Rule name cannot be empty");
        }

        if (ruleData.getExpression() == null || ruleData.getExpression().trim().isEmpty()) {
            throw new IllegalArgumentException("Rule expression cannot be empty");
        }

        if (ruleData.getInputEventType() == null || ruleData.getInputEventType().trim().isEmpty()) {
            throw new IllegalArgumentException("Input event type cannot be empty");
        }

        // Validate parameter types
        if (ruleData.getParameters() != null) {
            for (var param : ruleData.getParameters()) {
                if (param.getType() != null &&
                        !param.getType().matches("^(number|string|boolean)$")) {
                    throw new IllegalArgumentException("Parameter type must be 'number', 'string', or 'boolean'");
                }
            }
        }
    }
}
