package com.example.eventapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import org.springframework.scheduling.annotation.Async;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void sendRegistrationEmail(String to, String eventTitle, String date, String time, String venue) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Registration Confirmed - " + eventTitle);
            message.setText("Hi,\n\nYou have successfully registered for " + eventTitle + "!\n" +
                    "\n📍 Venue: " + venue +
                    "\n📅 Date: " + date +
                    "\n⏰ Time: " + time +
                    "\n\nThanks,\nEvent Team");

            mailSender.send(message);
        } catch (Exception e) {
            System.out.println("❌ Failed to send email: " + e.getMessage());
        }
    }
}