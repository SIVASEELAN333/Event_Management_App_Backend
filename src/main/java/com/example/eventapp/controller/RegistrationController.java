package com.example.eventapp.controller;

import com.example.eventapp.model.Registration;
import com.example.eventapp.model.RegistrationResponse;
import com.example.eventapp.model.User;
import com.example.eventapp.repository.RegistrationRepository;
import com.example.eventapp.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/registrations")
@CrossOrigin(origins = "*")
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private RegistrationRepository registrationRepository;


    // Register for an event
    @PostMapping
    public ResponseEntity<RegistrationResponse> register(
            @RequestParam Long userId,
            @RequestParam String eventId,
            @RequestParam(required = false, defaultValue = "false") boolean waitingList) {

        RegistrationResponse response = registrationService.register(userId, eventId, waitingList);
        if (response.getMessage().toLowerCase().contains("success") || response.getMessage().toLowerCase().contains("waiting")) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }


    // Unregister from an event
    @DeleteMapping
    public ResponseEntity<String> unregister(@RequestParam Long userId, @RequestParam String eventId) {
        boolean success = registrationService.unregister(userId, eventId);
        if (success) {
            return ResponseEntity.ok("Unregistered successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Registration not found");
        }
    }

    // Get all registrations by user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Registration>> getByUser(@PathVariable Long userId) {
        List<Registration> registrations = registrationService.getRegistrationsByUser(userId);
        return ResponseEntity.ok(registrations);
    }

    // Get all participants (users) of an event
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<User>> getByEvent(@PathVariable String eventId) {
        List<User> participants = registrationService.getRegistrationsByEvent(eventId)
                .stream()
                .filter(reg -> !reg.isWaitingList())  // ✅ No error here now
                .map(Registration::getUser)           // ✅ Works now
                .collect(Collectors.toList());
        return ResponseEntity.ok(participants);
    }



    // Delete registration by registration id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRegistration(@PathVariable Long id) {
        registrationService.deleteRegistration(id);
        return ResponseEntity.noContent().build();  // 204 No Content
    }

    @GetMapping("/top-participants")
    public List<Map<String, Object>> getTopParticipants() {
        List<Object[]> results = registrationRepository.findTopParticipants();

        return results.stream().limit(10).map(obj -> {
            Map<String, Object> map = new HashMap<>();
            map.put("username", obj[0]);
            map.put("count", obj[1]);
            return map;
        }).collect(Collectors.toList());
    }

    @GetMapping("/count/{eventId}")
    public long countConfirmedRegistrations(@PathVariable String eventId) {
        return registrationRepository.countByEventIdAndWaitingList(eventId, false);
    }


    @GetMapping("/event/{eventId}/waiting-list")
    public ResponseEntity<List<User>> getWaitingList(@PathVariable String eventId) {
        List<User> waitingList = registrationService.getWaitingListByEvent(eventId);
        return ResponseEntity.ok(waitingList);
    }

}
