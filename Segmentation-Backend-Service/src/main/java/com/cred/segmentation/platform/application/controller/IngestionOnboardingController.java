package com.cred.segmentation.platform.application.controller;

import com.cred.segmentation.platform.application.dto.EventStreamOnboardingRequestDto;
import com.cred.segmentation.platform.application.dto.EventsStreamOutputDto;
import com.cred.segmentation.platform.application.service.DataIngestionOnboardingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/events/onboarding")
@CrossOrigin(origins = "*")
public class IngestionOnboardingController {

    @Autowired
    private DataIngestionOnboardingService dataIngestionOnboardingService;

    @PostMapping
    public ResponseEntity<EventsStreamOutputDto> createEvent(@Valid @RequestBody EventStreamOnboardingRequestDto createDto) {
        EventsStreamOutputDto createdEvent = dataIngestionOnboardingService.createEvent(createDto);
        return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventsStreamOutputDto> getEventById(@PathVariable Long id) {
        EventsStreamOutputDto event = dataIngestionOnboardingService.getEventById(id);
        if (event != null) {
            return ResponseEntity.ok(event);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<EventsStreamOutputDto>> getAllEvents() {
        List<EventsStreamOutputDto> events = dataIngestionOnboardingService.getAllEvents();
        return ResponseEntity.ok(events);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventsStreamOutputDto> updateEvent(@PathVariable Long id,
                                                        @Valid @RequestBody EventStreamOnboardingRequestDto updateDto) {
        EventsStreamOutputDto updatedEvent = dataIngestionOnboardingService.updateEvent(id, updateDto);
        if (updatedEvent != null) {
            return ResponseEntity.ok(updatedEvent);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        dataIngestionOnboardingService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}
