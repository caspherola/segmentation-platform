package com.cred.segmentation.platform.application.repository;

import com.cred.segmentation.platform.application.entity.PipelineConfigurationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface PipelineConfigurationRepository extends JpaRepository<PipelineConfigurationEntity, String> {

    @Query("SELECT p FROM PipelineConfigurationEntity p WHERE p.partitionKey = :partitionKey")
    Optional<PipelineConfigurationEntity> findByPartitionKey(@Param("partitionKey") String partitionKey);

    @Query("SELECT p FROM PipelineConfigurationEntity p WHERE p.partitionKey LIKE %:keyPattern%")
    List<PipelineConfigurationEntity> findByPartitionKeyContaining(@Param("keyPattern") String keyPattern);

    @Query(value = "SELECT * FROM pipeline_configuration WHERE config_data ->> 'ruleSetInfo' ->> 'id' = :ruleId",
            nativeQuery = true)
    List<PipelineConfigurationEntity> findByRuleId(@Param("ruleId") String ruleId);

    @Query(value = "SELECT * FROM pipeline_configuration WHERE config_data ->> 'ruleSetInfo' ->> 'name' = :ruleName",
            nativeQuery = true)
    List<PipelineConfigurationEntity> findByRuleName(@Param("ruleName") String ruleName);

    boolean existsByPartitionKey(String partitionKey);
}