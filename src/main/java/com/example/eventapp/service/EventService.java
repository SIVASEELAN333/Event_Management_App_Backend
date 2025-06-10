package com.example.eventapp.service;

import com.example.eventapp.model.EventDocument;
import com.example.eventapp.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    public List<EventDocument> getAllEvents() {
        return eventRepository.findAll();
    }

    public EventDocument createEvent(EventDocument event) {
        return eventRepository.save(event);
    }

    public Optional<EventDocument> getEventById(String id) {
        return eventRepository.findById(id);
    }
    public EventDocument updateEvent(String id, EventDocument updatedEvent) {
        return eventRepository.findById(id).map(existingEvent -> {
            existingEvent.setId(updatedEvent.getId());
            existingEvent.setTitle(updatedEvent.getTitle());
            existingEvent.setDescription(updatedEvent.getDescription());
            existingEvent.setDate(updatedEvent.getDate());
            existingEvent.setTime(updatedEvent.getTime());
            existingEvent.setVenue(updatedEvent.getVenue());
            existingEvent.setOrganizer(updatedEvent.getOrganizer());
            existingEvent.setImageUrl(updatedEvent.getImageUrl());
            existingEvent.setParticipants(updatedEvent.getParticipants());
            existingEvent.setAllowedParticipants(updatedEvent.getAllowedParticipants());
            return eventRepository.save(existingEvent);
        }).orElse(null);
    }



    public EventDocument addParticipant(String eventId, String participantId) {
        Optional<EventDocument> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isEmpty()) return null;

        EventDocument event = optionalEvent.get();

        List<String> allowed = event.getAllowedParticipants();
        if (allowed != null && !allowed.isEmpty() && !allowed.contains(participantId)) {
            throw new RuntimeException("User is not allowed to participate in this event");
        }

        if (!event.getParticipants().contains(participantId)) {
            event.getParticipants().add(participantId);
            return eventRepository.save(event);
        }

        return event;
}
}