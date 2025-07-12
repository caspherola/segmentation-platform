package com.cred.segmentation.platform.application.mapper;

import com.cred.segmentation.platform.application.model.RuleDefinitionRequest;
import com.cred.segmentation.platform.application.dto.RuleDefinitionResponse;
import com.cred.segmentation.platform.application.entity.RuleDefinition;
import com.cred.segmentation.platform.application.entity.RuleParameter;
import com.cred.segmentation.platform.application.model.SegmentCreationRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper(componentModel = "spring")
public interface RuleDefinitionMapper {

    RuleDefinitionMapper INSTANCE = Mappers.getMapper(RuleDefinitionMapper.class);

    @Mapping(source = "ruleDefinitionRequest.ruleId", target = "ruleId")
    @Mapping(source = "ruleDefinitionRequest.name", target = "name")
    @Mapping(source = "ruleDefinitionRequest.description", target = "description")
    @Mapping(source = "ruleDefinitionRequest.inputEventType", target = "inputEventType")
    @Mapping(source = "ruleDefinitionRequest.expression", target = "expression")
    @Mapping(source = "ruleDefinitionRequest.enabled", target = "enabled")
    RuleDefinition toEntity(SegmentCreationRequest request);

    @Mapping(source = "ruleDefinitionRequest.ruleId", target = "ruleId")
    @Mapping(source = "ruleDefinitionRequest.name", target = "name")
    @Mapping(source = "ruleDefinitionRequest.description", target = "description")
    @Mapping(source = "ruleDefinitionRequest.inputEventType", target = "inputEventType")
    @Mapping(source = "ruleDefinitionRequest.expression", target = "expression")
    @Mapping(source = "ruleDefinitionRequest.enabled", target = "enabled")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(SegmentCreationRequest request, @MappingTarget RuleDefinition entity);

    RuleDefinitionResponse toResponse(RuleDefinition entity);

    List<RuleDefinitionResponse> toResponseList(List<RuleDefinition> entities);

    RuleParameter toParameterEntity(RuleDefinitionRequest.Parameter parameterData);

    RuleDefinitionResponse.ParameterResponse toParameterResponse(RuleParameter parameter);

    List<RuleParameter> toParameterEntityList(List<RuleDefinitionRequest.Parameter> parameterDataList);

    List<RuleDefinitionResponse.ParameterResponse> toParameterResponseList(List<RuleParameter> parameters);

    default String mapDefaultValue(Object defaultValue) {
        return defaultValue != null ? defaultValue.toString() : null;
    }
}
