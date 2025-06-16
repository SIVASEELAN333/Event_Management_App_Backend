package com.example.eventapp.service;

import com.example.eventapp.model.EventDocument;
import com.example.eventapp.model.Registration;
import com.example.eventapp.model.User;
import com.example.eventapp.repository.EventRepository;
import com.example.eventapp.repository.RegistrationRepository;
import com.example.eventapp.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class EventNotificationService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    // Runs every minute (cron format: sec min hour day month weekday)
    @Scheduled(cron = "0 * * * * *")
    public void sendReminderEmails() {
        LocalDateTime now = LocalDateTime.now();

        List<EventDocument> upcomingEvents = eventRepository.findAll().stream()
                .filter(e -> {
                    try {
                        LocalDate eventDate = LocalDate.parse(e.getDate());
                        LocalTime eventTime = LocalTime.parse(e.getTime());
                        LocalDateTime eventDateTime = LocalDateTime.of(eventDate, eventTime);

                        long minutesUntilEvent = Duration.between(now, eventDateTime).toMinutes();

                        return minutesUntilEvent >= 1439 && minutesUntilEvent <= 1441;
                        // 1440 minutes = 24 hours, so we allow ±1 minute buffer
                    } catch (Exception ex) {
                        System.out.println("❌ Failed to parse date/time for event: " + e.getTitle());
                        return false;
                    }
                }).toList();

        for (EventDocument event : upcomingEvents) {
            List<Registration> registrations = registrationRepository.findByEventId(event.getId());

            for (Registration reg : registrations) {
                User user = userRepository.findById(reg.getUserId()).orElse(null);
                if (user != null) {
                    emailService.sendReminderEmail(
                            user.getEmail(),
                            event.getTitle(),
                            event.getDate(),
                            event.getTime()
                    );
                    System.out.println("✅ Reminder email sent to: " + user.getEmail());
                }
            }
        }
    }
}
