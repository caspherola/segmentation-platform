package com.cred.segmentation.platform.application.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "rule_parameters")
public class RuleParameter {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    @NotBlank(message = "Parameter name cannot be blank")
    private String name;

    @Column(nullable = false)
    @Pattern(regexp = "^(number|string|boolean)$", message = "Type must be 'number', 'string', or 'boolean'")
    private String type;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "rule_definition_id", nullable = false)
    private UUID ruleDefinitionId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Constructors
    public RuleParameter() {
    }

    public RuleParameter(String name, String type, String description, UUID ruleDefinitionId) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.ruleDefinitionId = ruleDefinitionId;
    }
}
