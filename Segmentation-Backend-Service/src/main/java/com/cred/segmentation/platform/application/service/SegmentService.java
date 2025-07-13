package com.cred.segmentation.platform.application.service;

import com.cred.segmentation.platform.application.model.SegmentInsight;
import com.cred.segmentation.platform.application.model.UserSegments;

import java.util.Optional;
import java.util.Set;

public interface SegmentService {
    void processSegmentInsight(SegmentInsight insight);
    Optional<UserSegments> getUserSegments(String userId);
    boolean isUserInSegment(String userId, String segmentId);
    Set<String> getAllSegmentsForUser(String userId);
}
