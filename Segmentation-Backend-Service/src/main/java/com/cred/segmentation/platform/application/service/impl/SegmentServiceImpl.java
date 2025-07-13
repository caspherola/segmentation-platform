package com.cred.segmentation.platform.application.service.impl;

import com.cred.segmentation.platform.application.cache.CacheStrategy;
import com.cred.segmentation.platform.application.model.SegmentInsight;
import com.cred.segmentation.platform.application.model.UserSegments;
import com.cred.segmentation.platform.application.service.SegmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
@Service
@RequiredArgsConstructor
@Slf4j
public class SegmentServiceImpl implements SegmentService {
    private final CacheStrategy cacheStrategy;

    @Override
    public void processSegmentInsight(SegmentInsight insight) {
        try {
            log.info("Processing segment insight: userId={}, segmentId={}, insightId={}",
                    insight.getUserId(), insight.getSegmentId(), insight.getInsightId());

            cacheStrategy.addUserToSegment(insight.getUserId(), insight.getSegmentId());

            log.info("Successfully processed segment insight for user {} in segment {}",
                    insight.getUserId(), insight.getSegmentId());
        } catch (Exception e) {
            log.error("Error processing segment insight: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process segment insight", e);
        }
    }

    @Override
    public Optional<UserSegments> getUserSegments(String userId) {
        log.debug("Retrieving segments for user: {}", userId);
        return cacheStrategy.getUserSegments(userId);
    }

    @Override
    public boolean isUserInSegment(String userId, String segmentId) {
        log.debug("Checking if user {} is in segment {}", userId, segmentId);
        return cacheStrategy.isUserInSegment(userId, segmentId);
    }

    @Override
    public Set<String> getAllSegmentsForUser(String userId) {
        log.debug("Getting all segments for user: {}", userId);
        return cacheStrategy.getAllSegmentsForUser(userId);
    }
}
