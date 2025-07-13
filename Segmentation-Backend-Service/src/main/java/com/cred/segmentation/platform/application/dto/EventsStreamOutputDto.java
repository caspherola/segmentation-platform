package com.cred.segmentation.platform.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventsStreamOutputDto {
    private Long id;
    private String eventType;
    private String topicName;
    private String schema;
}
