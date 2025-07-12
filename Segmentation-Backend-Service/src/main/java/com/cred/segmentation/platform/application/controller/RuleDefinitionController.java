package com.cred.segmentation.platform.application.controller;

import com.cred.segmentation.platform.application.model.RuleDefinitionRequest;
import com.cred.segmentation.platform.application.dto.RuleDefinitionResponse;
import com.cred.segmentation.platform.application.model.SegmentCreationRequest;
import com.cred.segmentation.platform.application.service.RuleDefinitionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/rules")
public class RuleDefinitionController {

    private final RuleDefinitionService service;

    @Autowired
    public RuleDefinitionController(RuleDefinitionService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<RuleDefinitionResponse> createRule(@Valid @RequestBody SegmentCreationRequest request) {
        RuleDefinitionResponse response = service.createRule(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<RuleDefinitionResponse> getRuleById(@PathVariable UUID id) {
//        RuleDefinitionResponse response = service.getRuleById(id);
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping("/rule/{ruleId}")
//    public ResponseEntity<RuleDefinitionResponse> getRuleByRuleId(@PathVariable String ruleId) {
//        RuleDefinitionResponse response = service.getRuleByRuleId(ruleId);
//        return ResponseEntity.ok(response);
//    }

    @GetMapping
    public ResponseEntity<List<RuleDefinitionResponse>> getAllRules(
            @RequestParam(required = false) Boolean enabled,
            @RequestParam(required = false) String inputEventType) {

        List<RuleDefinitionResponse> responses;

        if (enabled != null && inputEventType != null) {
            responses = service.getRulesByInputEventType(inputEventType)
                    .stream()
                    .filter(rule -> rule.getEnabled().equals(enabled))
                    .toList();
        } else if (enabled != null) {
            responses = service.getRulesByEnabled(enabled);
        } else if (inputEventType != null) {
            responses = service.getRulesByInputEventType(inputEventType);
        } else {
            responses = service.getAllRules();
        }

        return ResponseEntity.ok(responses);
    }

//    @PutMapping("/{id}")
//    public ResponseEntity<RuleDefinitionResponse> updateRule(
//            @PathVariable UUID id,
//            @Valid @RequestBody SegmentCreationRequest request) {
//        RuleDefinitionResponse response = service.updateRule(id, request);
//        return ResponseEntity.ok(response);
//    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRule(@PathVariable UUID id) {
        service.deleteRule(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/rule/{ruleId}")
    public ResponseEntity<Void> deleteRuleByRuleId(@PathVariable String ruleId) {
        service.deleteRuleByRuleId(ruleId);
        return ResponseEntity.noContent().build();
    }
}
