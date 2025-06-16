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

    public RegistrationResponse register(Long userId, String eventId, boolean waitingList) {
        Optional<EventDocument> eventOpt = eventRepository.findById(eventId);
        if (eventOpt.isEmpty()) {
            return new RegistrationResponse("Event not found.", userId, eventId, null, null, false);
        }

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return new RegistrationResponse("User not found.", userId, eventId, null, null, false);
        }

        User user = userOpt.get();
        String userEmail = user.getEmail();
        String username = user.getUsername();

        EventDocument event = eventOpt.get();

        // Check capacity
        if (event.getCapacity() != null) {
            long currentCount = registrationRepository.countByEventIdAndWaitingListFalse(eventId);
            if (currentCount >= event.getCapacity()) {
                if (!waitingList) {
                    return new RegistrationResponse("Event is already full.", userId, eventId, null, null, false);
                } else {
                    if (registrationRepository.existsByUserIdAndEventId(userId, eventId)) {
                        return new RegistrationResponse("Already registered or on waiting list.", userId, eventId, null, null, true);
                    }

                    Registration reg = new Registration();
                    reg.setUserId(userId);
                    reg.setEventId(eventId);
                    reg.setWaitingList(true);
                    String timestamp = Instant.now().toString();
                    reg.setTimestamp(timestamp);
                    reg.setUserEmail(userEmail);
                    registrationRepository.save(reg);

                    return new RegistrationResponse("Added to waiting list.", userId, eventId, timestamp, reg.getId(), true);
                }
            }
        }

        // Check eligibility
        List<String> allowed = event.getAllowedParticipants();
        if (allowed != null && !allowed.isEmpty() && !allowed.contains(userEmail)) {
            return new RegistrationResponse("You are not eligible to register for this event.", userId, eventId, null, null, false);
        }

        // Check if already registered
        if (registrationRepository.existsByUserIdAndEventId(userId, eventId)) {
            Registration existingReg = registrationRepository.findByUserIdAndEventId(userId, eventId);
            return new RegistrationResponse("You already registered for this event.", userId, eventId, existingReg.getTimestamp(), existingReg.getId(), existingReg.isWaitingList());
        }

        // Add participant
        List<String> participants = event.getParticipants();
        if (participants == null) {
            participants = new ArrayList<>();
            event.setParticipants(participants);
        }

        if (participants.contains(userId.toString())) {
            return new RegistrationResponse("You are already listed as a participant for this event.", userId, eventId, null, null, false);
        }

        Registration registration = new Registration();
        registration.setUserId(userId);
        registration.setEventId(eventId);
        registration.setWaitingList(false);
        String timestamp = Instant.now().toString();
        registration.setTimestamp(timestamp);
        registration.setUserEmail(userEmail);
        registrationRepository.save(registration);

        participants.add(userId.toString());
        eventRepository.save(event);

        // Send confirmation email with ticket
        try {
            emailService.sendRegistrationEmailWithTicket(
                    userEmail,
                    event.getTitle(),
                    username,
                    event.getDate(),
                    event.getTime(),
                    event.getVenue(),
                    event.getOrganizer()
            );
        } catch (Exception e) {
            System.err.println("Failed to send email with ticket: " + e.getMessage());
        }

        return new RegistrationResponse("You registered successfully for the event.", userId, eventId, timestamp, registration.getId(), false);
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
            }

            // ✅ Promote first waiting list user (FIFO)
            List<Registration> waitingList = registrationRepository.findByEventIdAndWaitingList(eventId, true);
            if (!waitingList.isEmpty()) {
                Registration promoted = waitingList.get(0);
                promoted.setWaitingList(false);
                promoted.setTimestamp(Instant.now().toString());
                registrationRepository.save(promoted);

                if (participants == null) {
                    participants = new ArrayList<>();
                }
                participants.add(promoted.getUserId().toString());
                event.setParticipants(participants);
                eventRepository.save(event);

                Optional<User> promotedUserOpt = userRepository.findById(promoted.getUserId());
                promotedUserOpt.ifPresent(promotedUser -> {
                    try {
                        emailService.sendRegistrationEmailWithTicket(
                                promotedUser.getEmail(),
                                event.getTitle(),
                                promotedUser.getUsername(),
                                event.getDate(),
                                event.getTime(),
                                event.getVenue(),
                                event.getOrganizer()
                        );
                    } catch (Exception e) {
                        System.err.println("Failed to send promotion email: " + e.getMessage());
                    }
                });
            } else {
                event.setParticipants(participants); // save updated list even if no promotion
                eventRepository.save(event);
            }
        }

        return true;
    }

    public List<Registration> getRegistrationsByUser(Long userId) {
        return registrationRepository.findByUserId(userId);
    }

    public List<Registration> getRegistrationsByEvent(String eventId) {
        return registrationRepository.findByEventId(eventId);
    }

    public void deleteRegistration(Long id) {
        registrationRepository.deleteById(id);
    }

    public List<User> getWaitingListByEvent(String eventId) {
        List<Registration> registrations = registrationRepository.findByEventIdAndWaitingList(eventId, true);
        return registrations.stream()
                .map(reg -> userRepository.findById(reg.getUserId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
}
