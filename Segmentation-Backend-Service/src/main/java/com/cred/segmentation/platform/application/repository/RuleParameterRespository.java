package com.cred.segmentation.platform.application.repository;

import com.cred.segmentation.platform.application.entity.RuleDefinition;
import com.cred.segmentation.platform.application.entity.RuleParameter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RuleParameterRespository extends JpaRepository<RuleParameter, UUID> {
}
