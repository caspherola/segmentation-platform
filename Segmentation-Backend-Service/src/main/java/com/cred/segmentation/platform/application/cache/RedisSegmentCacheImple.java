package com.cred.segmentation.platform.application.cache;

import com.cred.segmentation.platform.application.cache.CacheStrategy;
import com.cred.segmentation.platform.application.model.UserSegments;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisSegmentCacheImple implements CacheStrategy{
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String USER_SEGMENTS_KEY_PREFIX = "user:segments:";
    private static final String SEGMENT_USERS_KEY_PREFIX = "segment:users:";
    private static final long DEFAULT_TTL = 3600; // 1 hour

    @Override
    public void addUserToSegment(String userId, String segmentId) {
        try {
            String userKey = USER_SEGMENTS_KEY_PREFIX + userId;
            String segmentKey = SEGMENT_USERS_KEY_PREFIX + segmentId;

            // Add segment to user's segments set
            redisTemplate.opsForSet().add(userKey, segmentId);
            redisTemplate.expire(userKey, DEFAULT_TTL, TimeUnit.SECONDS);

            // Add user to segment's users set (for reverse lookup)
            redisTemplate.opsForSet().add(segmentKey, userId);
            redisTemplate.expire(segmentKey, DEFAULT_TTL, TimeUnit.SECONDS);

            // Update last modified timestamp
            String timestampKey = userKey + ":timestamp";
            redisTemplate.opsForValue().set(timestampKey, LocalDateTime.now().toString());
            redisTemplate.expire(timestampKey, DEFAULT_TTL, TimeUnit.SECONDS);

            log.debug("Added user {} to segment {}", userId, segmentId);
        } catch (Exception e) {
            log.error("Error adding user {} to segment {}: {}", userId, segmentId, e.getMessage());
        }
    }

    @Override
    public Optional<UserSegments> getUserSegments(String userId) {
        try {
            String userKey = USER_SEGMENTS_KEY_PREFIX + userId;
            String timestampKey = userKey + ":timestamp";

            Set<Object> segments = redisTemplate.opsForSet().members(userKey);
            String timestampStr = (String) redisTemplate.opsForValue().get(timestampKey);

            if (segments == null || segments.isEmpty()) {
                return Optional.empty();
            }

            Set<String> segmentIds = segments.stream()
                    .map(Object::toString)
                    .collect(java.util.stream.Collectors.toSet());

            LocalDateTime lastUpdated = timestampStr != null ?
                    LocalDateTime.parse(timestampStr) : LocalDateTime.now();

            return Optional.of(UserSegments.builder()
                    .userId(userId)
                    .segments(segmentIds)
                    .lastUpdated(lastUpdated)
                    .build());

        } catch (Exception e) {
            log.error("Error retrieving segments for user {}: {}", userId, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public boolean isUserInSegment(String userId, String segmentId) {
        try {
            String userKey = USER_SEGMENTS_KEY_PREFIX + userId;
            return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(userKey, segmentId));
        } catch (Exception e) {
            log.error("Error checking if user {} is in segment {}: {}", userId, segmentId, e.getMessage());
            return false;
        }
    }

    @Override
    public void invalidateUserCache(String userId) {
        try {
            String userKey = USER_SEGMENTS_KEY_PREFIX + userId;
            String timestampKey = userKey + ":timestamp";

            redisTemplate.delete(userKey);
            redisTemplate.delete(timestampKey);

            log.debug("Invalidated cache for user {}", userId);
        } catch (Exception e) {
            log.error("Error invalidating cache for user {}: {}", userId, e.getMessage());
        }
    }

    @Override
    public Set<String> getAllSegmentsForUser(String userId) {
        try {
            String userKey = USER_SEGMENTS_KEY_PREFIX + userId;
            Set<Object> segments = redisTemplate.opsForSet().members(userKey);

            if (segments == null) {
                return Set.of();
            }

            return segments.stream()
                    .map(Object::toString)
                    .collect(java.util.stream.Collectors.toSet());

        } catch (Exception e) {
            log.error("Error retrieving all segments for user {}: {}", userId, e.getMessage());
            return Set.of();
        }
    }
}
