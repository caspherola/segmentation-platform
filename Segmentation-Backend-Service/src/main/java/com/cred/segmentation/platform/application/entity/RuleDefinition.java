package com.cred.segmentation.platform.application.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "rule_definitions")
public class RuleDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "rule_id", unique = true, nullable = false)
    @NotBlank(message = "Rule ID cannot be blank")
    private String ruleId;

    @Column(nullable = false)
    @NotBlank(message = "Name cannot be blank")
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "input_event_type", nullable = false)
    @NotBlank(message = "Input event type cannot be blank")
    private String inputEventType;

    @Column(nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "Expression cannot be blank")
    private String expression;

    @Column(nullable = false)
    @NotNull(message = "Enabled status cannot be null")
    private Boolean enabled = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public RuleDefinition() {
    }

    public RuleDefinition(String ruleId, String name, String description,
                          String inputEventType, String expression, Boolean enabled) {
        this.ruleId = ruleId;
        this.name = name;
        this.description = description;
        this.inputEventType = inputEventType;
        this.expression = expression;
        this.enabled = enabled;
    }
}
