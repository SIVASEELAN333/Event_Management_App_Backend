package com.example.eventapp.controller;

import com.example.eventapp.model.Interest;
import com.example.eventapp.repository.InterestRepository;
import com.example.eventapp.service.InterestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/interests")
public class InterestController {

    @Autowired
    private InterestService interestService;

    @Autowired
    private InterestRepository interestRepository;

    @PostMapping("/toggle")
    public ResponseEntity<String> toggleInterest(@RequestBody Interest interest) {
        try {

            interestService.toggleInterest(interest.getUserId(), interest.getEventId());
            return ResponseEntity.ok("Toggled successfully");
        } catch (Exception e) {
            System.err.println("❌ Error while toggling interest:");
            e.printStackTrace();  // ✅ This will show the REAL ERROR in the terminal
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }


    @GetMapping("/{userId}")
    public List<Interest> getUserInterests(@PathVariable String userId) {
        return interestService.getInterestsByUser(userId);
    }

    @GetMapping("/event/{eventId}/count")
    public ResponseEntity<Long> getInterestCount(@PathVariable String eventId) {
        long count = interestRepository.countByEventId(eventId);
        return ResponseEntity.ok(count);
    }
}
