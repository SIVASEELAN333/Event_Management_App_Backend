package com.example.eventapp.controller;

import com.example.eventapp.model.EventDocument;
import com.example.eventapp.model.WinnerDTO;
import com.example.eventapp.model.WinnerRequest;
import com.example.eventapp.repository.EventRepository;
import com.example.eventapp.service.EmailService;
import com.example.eventapp.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "http://localhost:5173")
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private EventRepository eventRepository;


    @GetMapping
    public List<EventDocument> getAllEvents() {
        return eventService.getAllEvents();
    }

    @PostMapping
    public EventDocument createEvent(@RequestBody EventDocument event) {
        if (event.getCapacity() <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than 0.");
        }
        return eventService.createEvent(event);
    }
    @PutMapping("/{id}")
    public ResponseEntity<EventDocument> updateEvent(@PathVariable String id, @RequestBody EventDocument updatedEvent) {
        EventDocument event = eventService.updateEvent(id, updatedEvent);
        if (event != null) {
            return ResponseEntity.ok(event);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable String id) {
        boolean deleted = eventService.deleteEvent(id);
        if (deleted) {
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404 Not Found
        }
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
    @PostMapping("/{eventId}/winner")
    public ResponseEntity<?> setWinner(
            @PathVariable String eventId,
            @RequestBody WinnerDTO winnerDto
    ) {
        // Validate event exists
        Optional<EventDocument> eventOpt = eventRepository.findById(eventId);
        if (eventOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found");
        }

        EventDocument event = eventOpt.get();
        event.setWinner(winnerDto);  // add a winner field to EventDocument
        eventRepository.save(event);

        return ResponseEntity.ok("Winner saved");
    }


}