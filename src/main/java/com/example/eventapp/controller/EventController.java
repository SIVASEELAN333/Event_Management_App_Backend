package com.example.eventapp.controller;

import com.example.eventapp.model.EventDocument;
import com.example.eventapp.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "*")
public class EventController {

    @Autowired
    private EventService eventService;

    @GetMapping
    public List<EventDocument> getAllEvents() {
        return eventService.getAllEvents();
    }

    @PostMapping
    public EventDocument createEvent(@RequestBody EventDocument event) {
        return eventService.createEvent(event);
    }

    @PostMapping("/{id}/participants")
    public ResponseEntity<?> addParticipant(@PathVariable String id, @RequestBody String participantId) {
        try {
            EventDocument updatedEvent = eventService.addParticipant(id, participantId);
            return ResponseEntity.ok(updatedEvent);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage()); // Forbidden
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable String id) {
        boolean deleted = eventService.deleteEvent(id);
        if (deleted) {
            return ResponseEntity.ok("Event deleted successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable String id, @RequestBody EventDocument updatedEvent) {
        EventDocument event = eventService.updateEvent(id, updatedEvent);
        if (event != null) {
            return ResponseEntity.ok(event);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
