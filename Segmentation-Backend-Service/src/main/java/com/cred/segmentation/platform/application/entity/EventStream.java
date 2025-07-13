package com.cred.segmentation.platform.application.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(name = "events")
@Data
public class EventStream {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Event type is required")
    @Size(max = 100, message = "Event type must not exceed 100 characters")
    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    @NotBlank(message = "Topic name is required")
    @Size(max = 200, message = "Topic name must not exceed 200 characters")
    @Column(name = "topic_name", nullable = false, length = 200)
    private String topicName;

    @NotBlank(message = "Schema is required")
    @Column(name = "schema", nullable = false, columnDefinition = "TEXT")
    private String schema;

    // Constructors
    public EventStream() {}

    public EventStream(String eventType, String topicName, String schema) {
        this.eventType = eventType;
        this.topicName = topicName;
        this.schema = schema;
    }
}
