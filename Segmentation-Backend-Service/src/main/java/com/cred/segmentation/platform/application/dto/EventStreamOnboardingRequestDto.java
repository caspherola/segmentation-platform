package com.cred.segmentation.platform.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventStreamOnboardingRequestDto {
    @NotBlank(message = "Event type is required")
    @Size(max = 100, message = "Event type must not exceed 100 characters")
    private String eventType;

    @NotBlank(message = "Topic name is required")
    @Size(max = 200, message = "Topic name must not exceed 200 characters")
    private String topicName;

    @NotBlank(message = "Schema is required")
    private String schema;
}
