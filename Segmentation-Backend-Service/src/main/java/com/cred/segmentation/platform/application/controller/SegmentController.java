package com.cred.segmentation.platform.application.controller;


import com.cred.segmentation.platform.application.model.UserSegments;
import com.cred.segmentation.platform.application.service.SegmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/segments")
@RequiredArgsConstructor
@Slf4j
public class SegmentController {

    private final SegmentService segmentService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<UserSegments> getUserSegments(@PathVariable String userId) {
        log.info("GET request for user segments: {}", userId);

        return segmentService.getUserSegments(userId)
                .map(segments -> {
                    log.info("Found {} segments for user {}", segments.getSegments().size(), userId);
                    return ResponseEntity.ok(segments);
                })
                .orElseGet(() -> {
                    log.info("No segments found for user {}", userId);
                    return ResponseEntity.notFound().build();
                });
    }

    @GetMapping("/user/{userId}/segments")
    public ResponseEntity<Set<String>> getAllSegmentsForUser(@PathVariable String userId) {
        log.info("GET request for all segments for user: {}", userId);

        Set<String> segments = segmentService.getAllSegmentsForUser(userId);

        if (segments.isEmpty()) {
            log.info("No segments found for user {}", userId);
            return ResponseEntity.notFound().build();
        }

        log.info("Found {} segments for user {}", segments.size(), userId);
        return ResponseEntity.ok(segments);
    }

    @GetMapping("/user/{userId}/segment/{segmentId}")
    public ResponseEntity<Map<String, Object>> isUserInSegment(
            @PathVariable String userId,
            @PathVariable String segmentId) {

        log.info("GET request to check if user {} is in segment {}", userId, segmentId);

        boolean isInSegment = segmentService.isUserInSegment(userId, segmentId);

        Map<String, Object> response = Map.of(
                "userId", userId,
                "segmentId", segmentId,
                "isInSegment", isInSegment
        );

        log.info("User {} is {} in segment {}", userId, isInSegment ? "present" : "not present", segmentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "segment-service"));
    }
}
