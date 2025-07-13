package com.cred.segmentation.platform.application.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SegmentationCreateUser {
    private String username;
    private String role;
}
