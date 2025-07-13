package com.cred.segmentation.platform.application.service.impl;

import com.cred.segmentation.platform.application.dto.EventStreamOnboardingRequestDto;
import com.cred.segmentation.platform.application.dto.EventsStreamOutputDto;
import com.cred.segmentation.platform.application.service.DataIngestionOnboardingService;
import com.cred.segmentation.platform.application.dto.EventsStreamOutputDto;
import com.cred.segmentation.platform.application.dto.EventsStreamOutputDto;
import com.cred.segmentation.platform.application.entity.EventStream;
import com.cred.segmentation.platform.application.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class DataIngestionOnboardingServiceImpl implements DataIngestionOnboardingService {

    @Autowired
    private EventRepository eventRepository;

    @Override
    public EventsStreamOutputDto createEvent(EventStreamOnboardingRequestDto createDto) {
        EventStream event = new EventStream(createDto.getEventType(), createDto.getTopicName(), createDto.getSchema());
        EventStream savedEvent = eventRepository.save(event);
        return convertToResponseDto(savedEvent);
    }

    @Override
    @Transactional(readOnly = true)
    public EventsStreamOutputDto getEventById(Long id) {
        Optional<EventStream> eventOpt = eventRepository.findById(id);
        if (eventOpt.isPresent()) {
            return convertToResponseDto(eventOpt.get());
        }
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventsStreamOutputDto> getAllEvents() {
        return eventRepository.findAll().stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventsStreamOutputDto updateEvent(Long id, EventStreamOnboardingRequestDto updateDto) {
        Optional<EventStream> eventOpt = eventRepository.findById(id);
        if (eventOpt.isPresent()) {
            EventStream existingEvent = eventOpt.get();
            existingEvent.setEventType(updateDto.getEventType());
            existingEvent.setTopicName(updateDto.getTopicName());
            existingEvent.setSchema(updateDto.getSchema());

            EventStream updatedEvent = eventRepository.save(existingEvent);
            return convertToResponseDto(updatedEvent);
        }
        return null;
    }

    @Override
    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    private EventsStreamOutputDto convertToResponseDto(EventStream event) {
        return new EventsStreamOutputDto(
                event.getId(),
                event.getEventType(),
                event.getTopicName(),
                event.getSchema()
        );
    }
}
