package com.cred.segmentation.platform.application.repository;

import com.cred.segmentation.platform.application.entity.RuleDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RuleDefinitionRepository extends JpaRepository<RuleDefinition, UUID> {

    Optional<RuleDefinition> findByRuleId(String ruleId);

    List<RuleDefinition> findByEnabled(Boolean enabled);

    List<RuleDefinition> findByInputEventType(String inputEventType);

    List<RuleDefinition> findByInputEventTypeAndEnabled(String inputEventType, Boolean enabled);

    @Query("SELECT r FROM RuleDefinition r WHERE r.name LIKE %:name%")
    List<RuleDefinition> findByNameContaining(@Param("name") String name);

    boolean existsByRuleId(String ruleId);
}

