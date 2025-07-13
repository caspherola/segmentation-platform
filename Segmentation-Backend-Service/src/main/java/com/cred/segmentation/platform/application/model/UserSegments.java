package com.cred.segmentation.platform.application.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSegments {
    private String userId;
    private Set<String> segments;
    private LocalDateTime lastUpdated;
}
