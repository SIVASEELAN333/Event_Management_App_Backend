package com.example.eventapp.service;

import com.example.eventapp.model.EventDocument;
import com.example.eventapp.model.Registration;
import com.example.eventapp.model.RegistrationResponse;
import com.example.eventapp.model.User;
import com.example.eventapp.repository.EventRepository;
import com.example.eventapp.repository.RegistrationRepository;
import com.example.eventapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RegistrationService {

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    public RegistrationResponse register(Long userId, String eventId) {
        Optional<EventDocument> eventOpt = eventRepository.findById(eventId);
        if (eventOpt.isEmpty()) {
            return new RegistrationResponse("Event not found.", userId, eventId, null, null);
        }

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return new RegistrationResponse("User not found.", userId, eventId, null, null);
        }

        User user = userOpt.get();
        String userEmail = user.getEmail();

        EventDocument event = eventOpt.get();

        List<String> allowed = event.getAllowedParticipants();
        if (allowed != null && !allowed.isEmpty() && !allowed.contains(userEmail)) {
            return new RegistrationResponse("You are not eligible to register for this event.", userId, eventId, null, null);
        }

        if (registrationRepository.existsByUserIdAndEventId(userId, eventId)) {
            Registration existingReg = registrationRepository.findByUserIdAndEventId(userId, eventId);
            return new RegistrationResponse("You already registered for this event.", userId, eventId, null, existingReg.getId());
        }

        List<String> participants = event.getParticipants();
        if (participants == null) {
            participants = new ArrayList<>();
            event.setParticipants(participants);
        }

        if (participants.contains(userId.toString())) {
            return new RegistrationResponse("You are already listed as a participant for this event.", userId, eventId, null, null);
        }

        Registration registration = new Registration();
        registration.setUserId(userId);
        registration.setEventId(eventId);
        String timestamp = Instant.now().toString();
        registration.setTimestamp(timestamp);
        registrationRepository.save(registration);

        participants.add(userId.toString());
        eventRepository.save(event);

        // Send registration confirmation email
        try {
            emailService.sendRegistrationEmail(
                    userEmail,
                    event.getTitle(),
                    event.getDate(),
                    event.getTime(),
                    event.getVenue()
            );
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }

        return new RegistrationResponse("You registered successfully for the event.", userId, eventId, timestamp, registration.getId());
    }

    public boolean unregister(Long userId, String eventId) {
        Registration reg = registrationRepository.findByUserIdAndEventId(userId, eventId);
        if (reg == null) {
            return false;
        }
        registrationRepository.delete(reg);

        Optional<EventDocument> eventOpt = eventRepository.findById(eventId);
        if (eventOpt.isPresent()) {
            EventDocument event = eventOpt.get();
            List<String> participants = event.getParticipants();
            if (participants != null) {
                participants.remove(userId.toString());
                eventRepository.save(event);
            }
        }
        return true;
    }

    public List<Registration> getRegistrationsByUser(Long userId) {
        return registrationRepository.findByUserId(userId);
    }

    public List<User> getRegistrationsByEvent(String eventId) {
        List<Registration> registrations = registrationRepository.findByEventId(eventId);
        return registrations.stream()
                .map(reg -> userRepository.findById(reg.getUserId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public void deleteRegistration(Long id) {
        registrationRepository.deleteById(id);
    }
}
