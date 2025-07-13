package com.cred.segmentation.platform.application.cache;

import com.cred.segmentation.platform.application.model.UserSegments;
import java.util.Optional;
import java.util.Set;

public interface CacheStrategy {
    void addUserToSegment(String userId, String segmentId);
    Optional<UserSegments> getUserSegments(String userId);
    boolean isUserInSegment(String userId, String segmentId);
    void invalidateUserCache(String userId);
    Set<String> getAllSegmentsForUser(String userId);
}
