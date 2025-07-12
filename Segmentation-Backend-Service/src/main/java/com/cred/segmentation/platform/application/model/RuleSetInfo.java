package com.cred.segmentation.platform.application.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RuleSetInfo {
    private String name;
    private String description;
    private String version;
    private String id;
}
