package com.cred.segmentation.platform.application.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SegmentInsight {

    @NotBlank
    @JsonProperty("insightId")
    private String insightId;

    @NotBlank
    @JsonProperty("segmentId")
    private String segmentId;

    @NotBlank
    @JsonProperty("userId")
    private String userId;

    @JsonProperty("createdAt")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private LocalDateTime createdAt;
}
