package com.cred.segmentation.platform.application.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.cred.segmentation.platform.application.model.SegmentInsight;
import com.cred.segmentation.platform.application.service.SegmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SegmentInsightConsumer {
    private final SegmentService segmentService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${app.kafka.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeSegmentInsight(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        try {
            log.info("Received message from topic: {}, partition: {}, offset: {}", topic, partition, offset);
            log.debug("Message content: {}", message);

            SegmentInsight insight = objectMapper.readValue(message, SegmentInsight.class);
            segmentService.processSegmentInsight(insight);

            log.info("Successfully processed segment insight message");
        } catch (Exception e) {
            log.error("Error processing Kafka message: {}", e.getMessage(), e);
            // In production, you might want to send to a dead letter queue
        }
    }
}
