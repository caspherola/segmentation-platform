package com.cred.segmentation.platform.application.service;

import com.cred.segmentation.platform.application.dto.EventStreamOnboardingRequestDto;
import com.cred.segmentation.platform.application.dto.EventsStreamOutputDto;

import java.util.List;

public interface DataIngestionOnboardingService {
    EventsStreamOutputDto createEvent(EventStreamOnboardingRequestDto createDto);

    EventsStreamOutputDto getEventById(Long id);

    List<EventsStreamOutputDto> getAllEvents();

    EventsStreamOutputDto updateEvent(Long id, EventStreamOnboardingRequestDto updateDto);

    void deleteEvent(Long id);
}
